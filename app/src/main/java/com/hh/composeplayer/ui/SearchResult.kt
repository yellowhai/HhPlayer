package com.hh.composeplayer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hh.composeplayer.R
import com.hh.composeplayer.bean.Model
import com.hh.composeplayer.ui.viewmodel.SearchResultViewModel
import com.hh.composeplayer.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @ProjectName: HHPlayer
 * @Package: com.hh.composeplayer.ui
 * @Description: 类描述
 * @Author: Hai Huang
 * @CreateDate: 2021/9/14  14:05
 */
@Composable
fun SearchResult(modifier: Modifier = Modifier, searchName: String) {
    val viewModel: SearchResultViewModel = viewModel()
    viewModel.searchName.value = searchName
    LaunchedEffect(viewModel) {
        withContext(Dispatchers.IO) {
            viewModel.appColor = SettingUtil.getColor()
        }
    }
    Column(modifier.fillMaxSize().navigationBarsPadding()) {
        CpTopBar(modifier, viewModel, viewModel.searchName.value!!)
        SearchResultContent(modifier, viewModel)
    }
    BoxProgress()
}

@Composable
fun SearchResultContent(modifier: Modifier = Modifier, viewModel: SearchResultViewModel) {
    val searchResultList = viewModel.getSearchResult().collectAsLazyPagingItems()
    SwipeRefresh(rememberSwipeRefreshState(viewModel.isRefreshing), onRefresh = {
        searchResultList.refresh()
    }) {
        BoxWithConstraints {
            when (searchResultList.loadState.refresh) {
                is LoadState.Loading -> {
                    boxProgress = true
                }
                is LoadState.Error -> {
                    ErrorBox(
                        modifier
                            .height(maxHeight)
                            .width(maxWidth), R.string.no_network
                    )
                    boxProgress = false
                }
                else -> {
                    when (searchResultList.itemCount) {
                        0 -> {
                            ErrorBox(
                                modifier
                                    .height(maxHeight)
                                    .width(maxWidth), R.string.no_data
                            )
                        }
                        else -> {
                            LazyColumn(modifier) {
                                items(searchResultList) {
                                    it?.let {
                                        Row(
                                            modifier
                                                .clickable {
                                                    viewModel.startComposeBundle(
                                                        Model.MovieDetail,
                                                        it.id.toString()
                                                    )
                                                }
                                                .padding(12.dp)) {
                                            Text(
                                                it.name!!, fontSize = 14.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                it.type!!, fontSize = 14.sp,
                                                modifier = modifier
                                                    .weight(1f)
                                                    .wrapContentWidth(Alignment.End),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )

                                        }
                                        Divider(
                                            modifier
                                                .fillMaxWidth()
                                                .padding(start = 12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    boxProgress = false
                }
            }
        }
    }
}