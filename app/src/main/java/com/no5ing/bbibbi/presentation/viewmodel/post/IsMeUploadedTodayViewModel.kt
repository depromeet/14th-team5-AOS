package com.no5ing.bbibbi.presentation.viewmodel.post

import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.no5ing.bbibbi.util.todayAsString
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IsMeUploadedTodayViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val localDataStorage: LocalDataStorage,
) : BaseViewModel<APIResponse<Boolean>>() {
    override fun initState(): APIResponse<Boolean> {
        return APIResponse.loading()
    }

    override fun invoke(arguments: Arguments) {
        viewModelScope.launch(Dispatchers.IO) {
            val me = localDataStorage.getMe()
            restAPI
                .getPostApi()
                .getPosts(
                    page = 1,
                    size = 1,
                    memberId = me?.memberId,
                    date = todayAsString()
                ).suspendOnSuccess {
                    val isMeUploadedToday = data.results.isNotEmpty()
                    setState(
                        APIResponse(
                            status = APIResponse.Status.SUCCESS,
                            data = isMeUploadedToday
                        )
                    )
                }
        }
    }

}