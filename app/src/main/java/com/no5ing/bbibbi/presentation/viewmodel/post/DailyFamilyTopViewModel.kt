package com.no5ing.bbibbi.presentation.viewmodel.post

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.no5ing.bbibbi.util.todayAsString
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class DailyFamilyTopViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<Map<String, Int>>() {
    override fun initState(): Map<String, Int> {
        return emptyMap()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO) {
            val newMap = HashMap<String, Int>()
            restAPI
                .getPostApi()
                .getPosts(
                    page = 1,
                    size = 100,
                    memberId = null,
                    date = todayAsString(),
                    sort = "ASC"
                ).suspendOnSuccess {
                    data.results.forEachIndexed { index, post ->
                        newMap[post.authorId] = index
                    }
                    setState(newMap)
                }
        }
    }

}