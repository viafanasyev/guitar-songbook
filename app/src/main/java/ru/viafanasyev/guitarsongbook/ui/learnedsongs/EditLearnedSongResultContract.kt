package ru.viafanasyev.guitarsongbook.ui.learnedsongs

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song
import ru.viafanasyev.guitarsongbook.utils.Extras

class EditLearnedSongResultContract : ActivityResultContract<Song, Song?>() {
    override fun createIntent(context: Context, input: Song): Intent {
        return Intent(context, EditLearnedSongActivity::class.java)
            .putExtra(Extras.SONG, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Song? {
        return intent.takeIf { resultCode == Activity.RESULT_OK }
            ?.getParcelableExtra(Extras.SONG)
    }
}