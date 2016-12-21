package pl.elpassion.common.extensions

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

fun AppCompatActivity.handleClickOnBackArrowItem(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
        onBackPressed()
        return true
    } else {
        return false
    }
}

fun AppCompatActivity.showBackArrowOnActionBar() {
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
}