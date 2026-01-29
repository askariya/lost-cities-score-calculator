package com.askariya.lostcitiesscorecalculator

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.askariya.lostcitiesscorecalculator.databinding.ActivityMainBinding
import com.askariya.lostcitiesscorecalculator.ui.playerboard.PlayerBoardPagerAdapter
import com.askariya.lostcitiesscorecalculator.ui.scoreboard.EndGameFragment
import com.askariya.lostcitiesscorecalculator.ui.settings.SettingsDialogFragment
import com.askariya.lostcitiesscorecalculator.ui.utils.DialogUtils
import com.askariya.lostcitiesscorecalculator.ui.utils.GameStateManager
import com.google.android.material.color.MaterialColors
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var colorPrimary: Int = 0
    private var showScoreOnSubmit: Boolean = false
    private var isBusy = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.header_toolbar))
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.custom_toolbar_title)
        // Remove default title
        supportActionBar?.title = ""

        // Fix the header and footer insets
        adjustHeaderAndFooterInsets()

        colorPrimary = MaterialColors.getColor(this, android.R.attr.colorPrimary, Color.BLACK)

        // Observe necessary external properties
        GameStateManager.gameOver.observe(this, endGameObserver)
        GameStateManager.showScoreboardOnSubmit.observe(this, showScoreOnSubmitObserver)
        GameStateManager.roundCounter.observe(this, roundCounterObserver)
        GameStateManager.player1Name.observe(this, player1NameObserver)
        GameStateManager.player2Name.observe(this, player2NameObserver)

        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        viewPager.adapter = PlayerBoardPagerAdapter(this)

        viewPager.offscreenPageLimit = 3

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customTabView = layoutInflater.inflate(R.layout.custom_tab_layout, null)
            val tabIcon = customTabView.findViewById<ImageView>(R.id.tab_icon)
            val tabText = customTabView.findViewById<TextView>(R.id.tab_text)

            tabText.text = when (position) {
                0 -> GameStateManager.player1Name.value
                1 -> GameStateManager.player2Name.value
                else -> getString(R.string.title_score_short)
            }
            tabIcon.setImageResource(when (position) {
                0 -> {
                    R.drawable.ic_player
                }
                1 -> {
                    R.drawable.ic_player
                }
                else -> {
                    R.drawable.ic_calculator_alternate
                }
            })

            tabIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            tab.customView = customTabView
        }.attach()

        // Set a listener for page change events to update action bar title
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setHeaderAndFooterToolbarColors(position)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val submitButton = menu.findItem(R.id.submit_button)
        submitButton.isVisible = !(GameStateManager.gameOver.value ?: false)
        return super.onPrepareOptionsMenu(menu)
    }


    // Handle toolbar button clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (isBusy)
            return true
        // Trigger haptic feedback
        findViewById<View>(R.id.header_toolbar)?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        return when (item.itemId) {
            R.id.submit_button -> {
                isBusy = true
                onSubmitButtonPressed()
                true
            }
            R.id.restart_game_button -> {
                isBusy = true
                onRestartGameButtonPressed()
                true
            }
            R.id.quit_button -> {
                isBusy = true
                onQuitButtonPressed()
                true
            }
            R.id.save_game_button -> {
                isBusy = true
                onSaveGameButtonPressed()
                true
            }
            R.id.load_game_button -> {
                isBusy = true
                onLoadGameButtonPressed()
                true
            }
            R.id.settings_button -> {
                isBusy = true
                onSettingsButtonPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun adjustHeaderAndFooterInsets()
    {
        ViewCompat.setOnApplyWindowInsetsListener(binding.headerToolbar) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom

            val typedValue = TypedValue()
            theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)

            // Partial top padding
            val reducedTopPadding = (statusBarHeight / 1.5).toInt().coerceAtLeast(0)

            // Partial bottom padding if you want
            val reducedBottomPadding = (navBarHeight / 1).toInt().coerceAtLeast(0)

            // Adjust toolbar
            view.updateLayoutParams {
                height = actionBarHeight + reducedTopPadding
            }
            view.updatePadding(top = reducedTopPadding)

            // Adjust bottom content so it doesn't overlap nav bar
            binding.viewPager.updatePadding(bottom = reducedBottomPadding)

            insets
        }
    }

    private fun setHeaderAndFooterToolbarColors(position: Int)
    {
        when (position) {
            0 -> {
                binding.headerToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.player1_colour))
                binding.container.setBackgroundColor(ContextCompat.getColor(this, R.color.player1_colour))  // purple background for player 1 tab
            }
            1 -> {
                binding.headerToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.player2_colour))
                binding.container.setBackgroundColor(ContextCompat.getColor(this, R.color.player2_colour))  // purple background for player 1 tab
            }
            else -> {
                binding.headerToolbar.setBackgroundColor(this.colorPrimary)
                binding.container.setBackgroundColor(this.colorPrimary)
            }
        }
    }

    private fun showEndGameFragment() {
        val endGameFragment = EndGameFragment()
        val transaction = supportFragmentManager.beginTransaction()

        // Apply rotate animations
        transaction.setCustomAnimations(
            R.anim.rotate_in,   // Animation for fragment entering
            R.anim.rotate_out   // Animation for fragment exiting
        )

        transaction.replace(R.id.endgame_fragment_container, endGameFragment)
        transaction.commit()

        // Hide ViewPager2 and TabLayout
        viewPager.visibility = View.GONE
        tabLayout.visibility = View.GONE
    }

    private fun hideEndGameFragment() {
        val endGameFragment = supportFragmentManager.findFragmentById(R.id.endgame_fragment_container)
        if (endGameFragment != null) {
            val transaction = supportFragmentManager.beginTransaction()

            // Apply rotate-out and slide-out animations
            transaction.setCustomAnimations(
                R.anim.rotate_in,   // Animation for fragment entering
                R.anim.slide_out   // Animation for fragment exiting
            )

            transaction.remove(endGameFragment)
            transaction.commit()
        }

        // Show ViewPager2 and TabLayout
        viewPager.visibility = View.VISIBLE
        tabLayout.visibility = View.VISIBLE
    }

    private fun animateTitleChange(newTitle: String) {
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)

        // Fade out old title
        toolbarTitle.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                // Change the text
                toolbarTitle.text = newTitle

                // Fade in new title
                toolbarTitle.alpha = 0f
                toolbarTitle.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
            }
            .start()
    }


    private fun updateActionBarTitle(title: String) {
//            supportActionBar?.title = title
        animateTitleChange(title)
    }

    private fun updateTabText(position: Int, newText: String) {
        val tab = tabLayout.getTabAt(position)
        val customTabView = tab?.customView
        val tabText = customTabView?.findViewById<TextView>(R.id.tab_text)
        tabText?.text = newText
    }

    private fun onSubmitButtonPressed() {
        GameStateManager.submitScore(this)
        isBusy = false
    }

    private fun onRestartGameButtonPressed() {
        GameStateManager.restartGame(this)
        isBusy = false
    }

    private fun onSaveGameButtonPressed() {
        GameStateManager.saveGame(this)
        isBusy = false
    }

    private fun onLoadGameButtonPressed() {
        GameStateManager.loadGame(this)
        isBusy = false
    }

    private fun onSettingsButtonPressed() {
        SettingsDialogFragment().show(supportFragmentManager, "SettingsDialog")
        isBusy = false
    }

    private fun onQuitButtonPressed() {
        val message = """
            Are you sure you want to quit the app?<br><br>
            <i>Unsaved player scores and round history will be lost.</i>
            """.trimIndent()
        DialogUtils.showConfirmationDialog(this,
            getString(R.string.label_quit),
            message,
            "Yes",
            "No")
        {
            finishAffinity()
        }
        isBusy = false
    }

    private val showScoreOnSubmitObserver = Observer<Boolean> { showScore ->
        showScoreOnSubmit = showScore
    }

    private val endGameObserver = Observer<Boolean> { gameOver ->
        if (gameOver) {
            showEndGameFragment()
            updateActionBarTitle("Game Over")
            setHeaderAndFooterToolbarColors(2)
            invalidateOptionsMenu()
        }
        else {
            hideEndGameFragment()
            viewPager.currentItem = 0
            invalidateOptionsMenu()
        }
    }

    private val roundCounterObserver = Observer<Int> { round ->
        updateActionBarTitle("${getString(R.string.round)} $round")

        // Jump to the Scoreboard tab if this is a user submitted score and setting is enabled
        if(showScoreOnSubmit && round != 1 && viewPager.currentItem != 2)
            viewPager.currentItem = 2
    }

    private val player1NameObserver = Observer<String> { name ->
        updateTabText(0, name)
    }

    private val player2NameObserver = Observer<String> { name ->
        updateTabText(1, name)
    }

}