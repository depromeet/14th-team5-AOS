package com.no5ing.bbibbi.presentation.viewmodel.members

import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FamilyMemberViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<Member>>() {

    override fun initState(): APIResponse<Member> {
        return APIResponse.loading()
    }

    override fun invoke(arguments: Arguments) {
        viewModelScope.launch(Dispatchers.IO) {
            val memberId = arguments.resourceId ?: throw RuntimeException()
            val member = restAPI.getMemberApi().getMember(memberId)
            setState(member.wrapToAPIResponse())
        }
    }

}