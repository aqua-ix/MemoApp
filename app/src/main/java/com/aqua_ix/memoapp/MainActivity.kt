package com.aqua_ix.memoapp

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import java.io.File

class MainActivity :
    AppCompatActivity(),
    FilesListFragment.OnFileSelectListener,
    InputFragment.OnFileOutputListener {

    // ナビゲーションドロワーの状態操作用オブジェクト
    private var drawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hasPermission()) setViews()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // ドロワーのトグルの状態の同期する
        drawerToggle?.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 状態の変化をドロワーに伝える
        drawerToggle?.onConfigurationChanged(newConfig)
    }

    private fun setupDrawer(drawer: DrawerLayout) {
        val toggle = ActionBarDrawerToggle(
            this, drawer, R.string.app_name, R.string.app_name
        )
        toggle.isDrawerIndicatorEnabled = true // ドロワーのトグルを有効にする
        drawer.addDrawerListener(toggle)       // 開いたり閉じたりのコールバックを設定する

        drawerToggle = toggle

        // アクションバーの設定を行う
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) //　ドロワー用のアイコンを表示
            setHomeButtonEnabled(true)
        }
    }

    // オプションメニューがタップされたときに呼ばれる
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // ドロワーに伝える
        return if (drawerToggle?.onOptionsItemSelected(item) == true) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }


    private fun setViews() {
        setContentView(R.layout.activity_main)

        // レイアウトからドロワーを探す
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)

        // レイアウト中にドロワーがある場合にだけ行う処理
        if (drawerLayout != null) {
            setupDrawer(drawerLayout)
        }
    }

    private fun hasPermission(): Boolean {
        // パーミッションを持っているか確認
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 持っていないならパーミッションを要求
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            setViews()
            drawerToggle?.syncState()
        } else {
            finish()
        }
    }

    override fun onFileSelected(file: File) {
        val fragment = supportFragmentManager.findFragmentById(R.id.input) as InputFragment
        fragment.show(file)
    }

    override fun onFileOutput() {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.list) as FilesListFragment
        fragment.show()
    }
}
