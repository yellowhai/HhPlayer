package com.hh.composeplayer.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.color.colorChooser
import com.hh.composeplayer.R
import com.hh.composeplayer.ui.viewmodel.SettingViewModel
import com.hh.composeplayer.util.*
import com.hh.composeplayer.util.ColorUtil.ACCENT_COLORS
import com.hh.composeplayer.util.ColorUtil.PRIMARY_COLORS_SUB
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @ProjectName: HelloComPose
 * @Package: com.hh.hellocompose.ui
 * @Description: 类描述
 * @Author: Hai Huang
 * @CreateDate: 2021/8/26  9:50
 */

@SuppressLint("CheckResult")
@Composable
fun CpSetting(modifier: Modifier = Modifier) {
    val lifecycleCoroutineScope = rememberCoroutineScope()
    val content = LocalContext.current
    val settingViewModel: SettingViewModel = viewModel()
    LaunchedEffect(settingViewModel) {
        withContext(IO) {
            settingViewModel.appColor = SettingUtil.getColor()
            settingViewModel.caCheSize = CacheDataManager.getTotalCacheSize(content)
        }
    }
    Column(modifier.fillMaxSize()) {
        CpTopBar(modifier, settingViewModel, stringResource(R.string.main_mine_setting))
        Text(
            stringResource(R.string.setting_basic_text),
            color = Color(settingViewModel.appColor),
            fontSize = 13.sp, modifier = modifier.padding(15.dp)
        )
        Column(
            modifier
                .fillMaxWidth()
                .clickable {
                    content.showMessage(
                        content.stringResource(R.string.confirm_clear_cache),
                        positiveButtonText = content.stringResource(R.string.confirm),
                        negativeButtonText = content.stringResource(R.string.cancel),
                        positiveAction = {
                            CacheDataManager.clearAllCache(content)
                            settingViewModel.caCheSize =
                                CacheDataManager.getTotalCacheSize(content)
                        })
                }
                .padding(15.dp)
        ) {
            Text(
                stringResource(R.string.setting_clear_cache),
                color = colorResource(R.color.text_color),
                fontSize = 15.sp
            )
            Text(
                settingViewModel.caCheSize,
                color = colorResource(R.color.forum_inactive_color),
                fontSize = 14.sp
            )
        }
        Column(
            modifier
                .fillMaxWidth()
                .clickable {
                    content.showMessage(
                        content.stringResource(R.string.confirm_exit_app),
                        positiveButtonText = content.stringResource(R.string.exit),
                        negativeButtonText = content.stringResource(R.string.cancel),
                        positiveAction = {
                            settingViewModel.onExit()
                        })
                }
                .padding(15.dp)) {
            Text(
                stringResource(R.string.exit),
                color = colorResource(R.color.text_color),
                fontSize = 15.sp
            )
            Text(
                stringResource(R.string.exit_app),
                color = colorResource(R.color.forum_inactive_color),
                fontSize = 14.sp
            )
        }
        Divider(
            modifier = modifier
                .height(1.dp)
                .fillMaxWidth()
        )
        Text(
            stringResource(R.string.setting_other_text),
            color = Color(settingViewModel.appColor),
            fontSize = 13.sp, modifier = modifier.padding(15.dp)
        )
        Row(
            modifier
                .clickable {
                    MaterialDialog(content).show {
                        title(R.string.setting_theme_color)
                        colorChooser(
                            ACCENT_COLORS,
                            initialSelection = settingViewModel.appColor,
                            subColors = PRIMARY_COLORS_SUB
                        ) { _, color ->
                            settingViewModel.appColor = color
                            lifecycleCoroutineScope.launch {
                                withContext(IO) {
                                    ///修改颜色
                                    SettingUtil.setColor(color)
                                }
                            }
                        }
                        getActionButton(WhichButton.POSITIVE).updateTextColor(
                            settingViewModel.appColor
                        )
                        getActionButton(WhichButton.NEGATIVE).updateTextColor(
                            settingViewModel.appColor
                        )
                        positiveButton(R.string.done)
                        negativeButton(R.string.cancel)
                    }
                }
                .padding(15.dp)) {
            Column {
                Text(
                    stringResource(R.string.setting_theme_color),
                    color = colorResource(R.color.text_color),
                    fontSize = 15.sp
                )
                Text(
                    stringResource(R.string.setting_theme_color_tips),
                    color = colorResource(R.color.forum_inactive_color),
                    fontSize = 14.sp
                )
            }
            Column(
                modifier
                    .fillMaxWidth()
                    .padding(end = 15.dp)
            ) {
                Spacer(
                    modifier
                        .size(48.dp)
                        .border(1.dp, Color.Black, CircleShape)
                        .clip(CircleShape)
                        .background(Color(settingViewModel.appColor))
                        .align(Alignment.End)
                )
            }
        }
    }
}