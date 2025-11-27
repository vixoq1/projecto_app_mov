package com.example.projecto_prueba_final.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecto_prueba_final.data.Faq
import com.example.projecto_prueba_final.data.FaqDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FaqFormState(
    val id: Int = 0,
    val question: String = "",
    val answer: String = ""
)

class FaqViewModel(private val dao: FaqDao) : ViewModel() {
    private val _form = MutableStateFlow(FaqFormState())
    val form: StateFlow<FaqFormState> = _form.asStateFlow()

    val faqs: StateFlow<List<Faq>> = dao.getAll()
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onFormChange(question: String, answer: String) {
        _form.update { it.copy(question = question, answer = answer) }
    }

    fun editar(faq: Faq?) {
        _form.update {
            it.copy(
                id = faq?.id ?: 0,
                question = faq?.question ?: "",
                answer = faq?.answer ?: ""
            )
        }
    }

    fun guardar() {
        viewModelScope.launch {
            val faq = Faq(
                id = form.value.id,
                question = form.value.question,
                answer = form.value.answer
            )
            dao.save(faq)
            limpiarForm()
        }
    }

    fun eliminar(faq: Faq) {
        viewModelScope.launch {
            dao.delete(faq)
        }
    }

    fun limpiarForm() {
        _form.update { FaqFormState() }
    }
}