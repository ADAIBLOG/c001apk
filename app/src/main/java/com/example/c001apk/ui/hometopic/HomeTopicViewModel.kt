package com.example.c001apk.ui.hometopic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c001apk.adapter.LoadingState
import com.example.c001apk.constant.Constants.LOADING_FAILED
import com.example.c001apk.logic.model.TopicBean
import com.example.c001apk.logic.repository.NetworkRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeTopicViewModel @Inject constructor(
    private val networkRepo: NetworkRepo
) : ViewModel() {

    var title: String? = null
    var url: String? = null
    var type: String? = null
    var isInit = true
    var tabList = ArrayList<String>()
    var position: Int = 0
    val topicList: MutableList<TopicBean> = ArrayList()
    var page = 1

    val loadingState = MutableLiveData<LoadingState>()

    fun fetchTopicList() {
        viewModelScope.launch(Dispatchers.IO) {
            networkRepo.getDataList(url.toString(), title.toString(), null, null, page)
                .collect { result ->
                    val topic = result.getOrNull()
                    if (topic != null) {
                        if (!topic.message.isNullOrEmpty()) {
                            loadingState.postValue(LoadingState.LoadingError(topic.message))
                            return@collect
                        } else if (!topic.data.isNullOrEmpty()) {
                            if (tabList.isEmpty()) {
                                topic.data[0].entities?.let { entities ->
                                    entities.forEach {
                                        tabList.add(it.title)
                                        topicList.add(TopicBean(it.url, it.title))
                                    }
                                }
                                loadingState.postValue(LoadingState.LoadingDone)
                            }
                        }
                    } else {
                        loadingState.postValue(LoadingState.LoadingFailed(LOADING_FAILED))
                        result.exceptionOrNull()?.printStackTrace()
                    }
                }
        }
    }

    fun fetchProductList() {
        viewModelScope.launch(Dispatchers.IO) {
            networkRepo.getProductList()
                .collect { result ->
                    val data = result.getOrNull()
                    if (data != null) {
                        if (!data.message.isNullOrEmpty()) {
                            loadingState.postValue(LoadingState.LoadingError(data.message))
                            return@collect
                        } else if (!data.data.isNullOrEmpty()) {
                            if (tabList.isEmpty()) {
                                data.data.let {
                                    it.forEach { item ->
                                        tabList.add(item.title)
                                        topicList.add(TopicBean(item.url, item.title))
                                    }
                                }
                                loadingState.postValue(LoadingState.LoadingDone)
                            }
                        }
                    } else {
                        loadingState.postValue(LoadingState.LoadingFailed(LOADING_FAILED))
                        result.exceptionOrNull()?.printStackTrace()
                    }
                }
        }
    }

}