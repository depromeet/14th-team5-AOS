package com.no5ing.bbibbi.presentation.viewmodel.members

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.data.repository.member.GetMembersRepository
import com.no5ing.bbibbi.di.SessionModule
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FamilyMembersViewModel @Inject constructor(
    private val getMembersRepository: GetMembersRepository,
    private val sessionModule: SessionModule,
) : BaseViewModel<PagingData<Member>>() {
    override fun initState(): PagingData<Member> {
        return PagingData.empty()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO) {
            getMembersRepository
                .fetch(arguments)
                .cachedIn(viewModelScope)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = PagingData.empty()
                ).combine(MutableStateFlow(sessionModule.sessionState.value.memberId)){ pagingData, memberId ->
                    pagingData.filter {
                        it.memberId != memberId
                    }
                }.collectLatest {
                    setState(it)
                }
        }
    }

    override fun release() {
        super.release()
        getMembersRepository.closeResources()
    }
}