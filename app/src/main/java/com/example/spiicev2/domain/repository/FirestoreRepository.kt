package com.example.spiicev2.domain.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class NotePostRepository(private val db: FirebaseFirestore) {
    companion object {
        const val NOTES = "notes"
    }

    // Получение всех заметок
    fun getAllNotes(): Flow<Result<List<NoteData>>> = flow {
        try {
            val result = db.collection(NOTES)
                .get()
                .await()
            val notes = result.mapNotNull { document ->
                val notePost = document.toObject(NoteData::class.java)
                notePost.copy(id = document.id)
            }
            emit(Result.success(notes))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /*// Получение заметки по ID
    fun getNoteById(noteId: String): Flow<Result<NotePost>> = flow {
        try {
            val document = db.collection(NOTES).document(noteId).get().await()
            if (document.exists()) {
                val notePost = document.toObject(NotePost::class.java)
                emit(Result.success(notePost!!))
            } else {
                emit(Result.failure(Exception("Note not found")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }*/

    // Добавление заметки
    suspend fun addNotePost(noteData: NoteData) {
        db.collection(NOTES).add(noteData).await()
    }

    // Обновление заметки
    suspend fun updateNotePost(noteId: String, noteData: NoteData) {
        db.collection(NOTES).document(noteId).set(noteData).await()
    }

    // Удаление заметки
    suspend fun deleteNotePost(noteId: String) {
        db.collection(NOTES).document(noteId).delete().await()
    }
}