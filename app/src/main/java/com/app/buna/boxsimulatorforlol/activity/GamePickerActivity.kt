package com.app.buna.boxsimulatorforlol.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.boxsimulatorforlol.R
import com.app.buna.boxsimulatorforlol.adapter.MiniGameAdapter
import com.app.buna.boxsimulatorforlol.databinding.ActivityGamePickerBinding
import com.app.buna.boxsimulatorforlol.dto.MiniGameItem
import com.app.buna.boxsimulatorforlol.game.cardgame.CardGameActivity
import com.app.buna.boxsimulatorforlol.game.hangman.HangmanActivity
import com.app.buna.boxsimulatorforlol.game.runninggame.RunningGameActivity

class GamePickerActivity : AppCompatActivity(), ClickListener {

    lateinit var binding: ActivityGamePickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game_picker)

        binding.minigameRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MiniGameAdapter(getMiniGame(), this@GamePickerActivity)
        }
    }

    private fun getMiniGame(): ArrayList<MiniGameItem> {
        val gameList = ArrayList<MiniGameItem>()

        val resArray = resources.obtainTypedArray(R.array.minigame_background)
        val titleArray = resources.getStringArray(R.array.game_names)

        for (i in titleArray.indices) {
            gameList.add(MiniGameItem(resArray.getResourceId(i, 0), titleArray[i]))
        }

        return gameList
    }

    override fun onClick(position: Int) {
        when (position) {
            0 -> {
                startActivity(Intent(this, CardGameActivity::class.java))
            }
            1 -> {
                startActivity(Intent(this, RunningGameActivity::class.java))
            }
            2 -> {
                startActivity(Intent(this, HangmanActivity::class.java))
            }
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in)
    }
}

interface ClickListener {
    fun onClick(position: Int)
}