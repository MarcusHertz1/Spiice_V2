package com.example.spiicev2.domain.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotePostRepository @Inject constructor(private val db: FirebaseFirestore) {

    companion object {
        private const val NOTES = "notes"
    }

    // Получение всех заметок
    fun getAllNotes(): Flow<Result<List<NoteData>>> = flow {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val result = db.collection(NOTES)
            .whereEqualTo("userId", uid)
            .get()
            .await()

        val notes = result.mapNotNull { document ->
            document.toObject(NoteData::class.java).copy(id = document.id)
        }

        emit(Result.success(notes))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // Добавление заметки
    suspend fun addNotePost(noteData: NoteData): Result<Unit> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        return try {
            db.collection(NOTES)
                .add(noteData.copy(userId = uid))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Обновление заметки
    suspend fun updateNotePost(noteId: String, noteData: NoteData): Result<Unit> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        return try {
            db.collection(NOTES)
                .document(noteId)
                .set(noteData.copy(userId = uid))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Удаление заметки
    suspend fun deleteNotePost(noteId: String): Result<Unit> {
        return try {
            db.collection(NOTES).document(noteId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
