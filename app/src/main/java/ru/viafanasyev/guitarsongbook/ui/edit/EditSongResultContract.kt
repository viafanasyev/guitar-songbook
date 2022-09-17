package ru.viafanasyev.guitarsongbook.ui.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.viafanasyev.guitarsongbook.domain.common.entities.SongListItem
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song
import ru.viafanasyev.guitarsongbook.utils.Extras

class EditSongResultContract : ActivityResultContract<SongListItem, Song?>() {
    override fun createIntent(context: Context, input: SongListItem): Intent {
        return Intent(context, AddAndEditSongActivity::class.java)
            .putExtra(Extras.SONG_LIST_ITEM, input)
            .putExtra(Extras.IS_LEARNED, input.isLearned)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Song? {
        return intent.takeIf { resultCode == Activity.RESULT_OK }
            ?.getParcelableExtra(Extras.SONG)
    }
}