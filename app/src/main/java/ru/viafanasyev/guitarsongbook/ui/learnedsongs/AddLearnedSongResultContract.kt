package ru.viafanasyev.guitarsongbook.ui.learnedsongs

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.viafanasyev.guitarsongbook.domain.entities.Song
import ru.viafanasyev.guitarsongbook.utils.Extras

class AddLearnedSongResultContract : ActivityResultContract<Int, Song?>() {
    override fun createIntent(context: Context, input: Int): Intent {
        return Intent(context, AddLearnedSongActivity::class.java)
            .putExtra(Extras.SONG_ID, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Song? {
        return intent.takeIf { resultCode == Activity.RESULT_OK }
            ?.getParcelableExtra(Extras.SONG)
    }
}