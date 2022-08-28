package ru.viafanasyev.guitarsongbook.ui.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song
import ru.viafanasyev.guitarsongbook.utils.Extras

class AddSongResultContract : ActivityResultContract<Boolean, Song?>() {
    override fun createIntent(context: Context, input: Boolean): Intent {
        return Intent(context, AddAndEditSongActivity::class.java)
            .putExtra(Extras.IS_LEARNED, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Song? {
        return intent.takeIf { resultCode == Activity.RESULT_OK }
            ?.getParcelableExtra(Extras.SONG)
    }
}