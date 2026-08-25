package com.aegisfit.app.presentation.screen.skincare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aegisfit.app.domain.model.CareCategory
import com.aegisfit.app.domain.model.CareItem
import com.aegisfit.app.domain.model.CareTimeSlot
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.DailyCareRepository
import com.aegisfit.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DailyCareState(
    val items: List<CareItem> = emptyList(),
    val selectedTimeSlotFilter: CareTimeSlot? = null,
    val showAddDialog: Boolean = false,
    val itemPendingDelete: CareItem? = null,
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = true
) {
    val progress: Float
        get() = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    val morningItems: List<CareItem>
        get() = items.filter { it.timeSlot == CareTimeSlot.MORNING }

    val afternoonItems: List<CareItem>
        get() = items.filter { it.timeSlot == CareTimeSlot.AFTERNOON }

    val eveningItems: List<CareItem>
        get() = items.filter { it.timeSlot == CareTimeSlot.EVENING }

    val nightItems: List<CareItem>
        get() = items.filter { it.timeSlot == CareTimeSlot.NIGHT }
}

@HiltViewModel
class SkincareViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dailyCareRepository: DailyCareRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DailyCareState())
    val state: StateFlow<DailyCareState> = _state.asStateFlow()

    private val userId: String
        get() = authRepository.currentUserId ?: ""

    init {
        loadDailyCareItems()
    }

    private fun loadDailyCareItems() {
        val today = DateUtils.todayStartMillis()
        viewModelScope.launch {
            dailyCareRepository.getCareItemsWithStatus(userId, today).collect { careItems ->
                val total = careItems.size
                val completed = careItems.count { it.isCompleted }
                _state.update {
                    it.copy(
                        items = careItems,
                        totalCount = total,
                        completedCount = completed,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleItem(itemId: Long, isCompleted: Boolean) {
        val today = DateUtils.todayStartMillis()
        viewModelScope.launch {
            dailyCareRepository.toggleCareItemCompletion(userId, today, itemId, isCompleted)
        }
    }

    fun addItem(
        title: String,
        timeSlot: CareTimeSlot,
        category: CareCategory,
        notes: String? = null,
        timeHint: String? = null
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            dailyCareRepository.addCareItem(
                userId = userId,
                title = title,
                timeSlot = timeSlot,
                category = category,
                notes = notes,
                timeHint = timeHint
            )
            _state.update { it.copy(showAddDialog = false) }
        }
    }

    fun deleteItem(itemId: Long) {
        viewModelScope.launch {
            dailyCareRepository.deleteCareItem(itemId)
            _state.update { it.copy(itemPendingDelete = null) }
        }
    }

    fun selectFilter(slot: CareTimeSlot?) {
        _state.update { it.copy(selectedTimeSlotFilter = slot) }
    }

    fun openAddDialog() {
        _state.update { it.copy(showAddDialog = true) }
    }

    fun dismissAddDialog() {
        _state.update { it.copy(showAddDialog = false) }
    }

    fun setItemPendingDelete(item: CareItem?) {
        _state.update { it.copy(itemPendingDelete = item) }
    }
}
