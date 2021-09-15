package com.hh.composeplayer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.TopAppBar
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hh.composeplayer.R
import com.hh.composeplayer.ui.viewmodel.SearchResultViewModel
import com.hh.composeplayer.util.ErrorBox
import com.hh.composeplayer.util.Mylog
import com.hh.composeplayer.util.SettingUtil
import com.hh.composeplayer.util.boxProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @ProjectName: HelloComPose
 * @Package: com.hh.composeplayer.ui
 * @Description: 类描述
 * @Author: Hai Huang
 * @CreateDate: 2021/9/14  14:05
 */
@Composable
fun SearchResult(modifier: Modifier = Modifier, searchName: String) {
    Mylog.e("HHLog", "SearchResult")
    val viewModel: SearchResultViewModel = viewModel()
    viewModel.let { model ->
        model.searchName.value = searchName
        LaunchedEffect(viewModel) {
            withContext(Dispatchers.IO) {
                model.appColor = SettingUtil.getColor()
            }
        }
    }
    Column(modifier.fillMaxSize()) {
        SearchResultToolBar(modifier, viewModel)
        SearchResultContent(modifier, viewModel)
    }
    BoxProgress()
}

@Composable
fun SearchResultContent(modifier: Modifier = Modifier, viewModel: SearchResultViewModel) {
    Mylog.e("HHLog", "SearchResultContent")
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
                    ErrorBox(modifier
                        .height(maxHeight)
                        .width(maxWidth),R.string.no_network)
                    boxProgress = false
                }
                else -> {
                    when (searchResultList.itemCount) {
                        0 -> {
                            ErrorBox(modifier
                                .height(maxHeight)
                                .width(maxWidth),R.string.no_data)
                        }
                        else -> {
                            LazyColumn(modifier) {
                                items(searchResultList) {
                                    Row(modifier.padding(12.dp)) {
                                        it?.let {
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
                    boxProgress = false
                }
            }

        }
    }
}

@Composable
fun SearchResultToolBar(modifier: Modifier = Modifier, viewModel: SearchResultViewModel) {
    Mylog.e("HHLog", "SearchResultToolBar")
    TopAppBar(
        {
            Text(viewModel.searchName.value, color = Color.White)
        },
        modifier = modifier,
        backgroundColor = Color(viewModel.appColor),
        contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.statusBars),
        navigationIcon = {
            IconButton(
                onClick = {
                    viewModel.onBackPressed()
                }
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "back", tint = Color.White)
            }
        },
        elevation = 20.dp,
    )
}