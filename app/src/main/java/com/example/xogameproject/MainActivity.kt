package com.example.xogameproject

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {
    
    // Game state variables
    private var currentPlayer = 'X'  // First player is X
    private val gameBoard = Array(3) { CharArray(3) { ' ' } }  // Empty 3x3 game board
    private var gameActive = true

    // UI elements
    private lateinit var statusTextView: TextView
    private lateinit var playAgainButton: Button
    private lateinit var boardButtons: Array<Array<Button>>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Initialize UI elements
        initializeGame()
    }
    
    private fun initializeGame() {
        // Get references to UI elements
        statusTextView = findViewById(R.id.statusTextView)
        playAgainButton = findViewById(R.id.playAgainButton)
        
        // Set up the play again button
        playAgainButton.setOnClickListener {
            resetGame()
        }
        
        // Initialize the board buttons with their click listeners
        boardButtons = Array(3) { row ->
            Array(3) { col ->
                val buttonId = resources.getIdentifier(
                    "button$row$col",
                    "id",
                    packageName
                )
                val button = findViewById<Button>(buttonId)
                button.setOnClickListener(this)
                button
            }
        }
        
        // Clear the game board
        resetGame()
    }
    
    private fun resetGame() {
        // Reset game state
        currentPlayer = 'X'
        gameActive = true
        
        // Clear the virtual game board
        for (row in 0..2) {
            for (col in 0..2) {
                gameBoard[row][col] = ' '
            }
        }
        
        // Reset UI
        for (row in boardButtons) {
            for (button in row) {
                button.text = ""
                button.isEnabled = true
                // Ensure buttons have default styling
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                button.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
        }
        
        // Update status text and hide play again button
        updateStatusText()
        playAgainButton.visibility = View.INVISIBLE
    }
    
    override fun onClick(view: View) {
        // Only process click if game is active
        if (!gameActive) return
        
        // Find which button was clicked
        for (row in 0..2) {
            for (col in 0..2) {
                if (view.id == boardButtons[row][col].id) {
                    // Make the move if the cell is empty
                    if (gameBoard[row][col] == ' ') {
                        makeMove(row, col)
                    }
                    return
                }
            }
        }
    }
    
    private fun makeMove(row: Int, col: Int) {
        // Update the virtual game board
        gameBoard[row][col] = currentPlayer
        
        // Update the UI
        boardButtons[row][col].text = currentPlayer.toString()
        
        // Set the appropriate color for X or O
        val colorId = if (currentPlayer == 'X') R.color.player_x else R.color.player_o
        boardButtons[row][col].setTextColor(ContextCompat.getColor(this, colorId))
        
        // Disable the button to prevent re-clicking
        boardButtons[row][col].isEnabled = false
        
        // Check for win or draw
        if (checkForWin()) {
            gameActive = false
            if (currentPlayer == 'X') {
                statusTextView.text = getString(R.string.player_x_wins)
                statusTextView.setTextColor(ContextCompat.getColor(this, R.color.player_x))
            } else {
                statusTextView.text = getString(R.string.player_o_wins)
                statusTextView.setTextColor(ContextCompat.getColor(this, R.color.player_o))
            }
            playAgainButton.visibility = View.VISIBLE
        } else if (isBoardFull()) {
            gameActive = false
            statusTextView.text = getString(R.string.game_draw)
            statusTextView.setTextColor(ContextCompat.getColor(this, R.color.status_text))
            playAgainButton.visibility = View.VISIBLE
        } else {
            // Switch to the other player
            currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
            updateStatusText()
        }
    }
    
    private fun updateStatusText() {
        val stringId = if (currentPlayer == 'X') R.string.player_x_turn else R.string.player_o_turn
        val colorId = if (currentPlayer == 'X') R.color.player_x else R.color.player_o
        
        statusTextView.text = getString(stringId)
        statusTextView.setTextColor(ContextCompat.getColor(this, colorId))
    }
    
    private fun checkForWin(): Boolean {
        // Check rows
        for (row in 0..2) {
            if (gameBoard[row][0] != ' ' && 
                gameBoard[row][0] == gameBoard[row][1] && 
                gameBoard[row][1] == gameBoard[row][2]) {
                return true
            }
        }
        
        // Check columns
        for (col in 0..2) {
            if (gameBoard[0][col] != ' ' && 
                gameBoard[0][col] == gameBoard[1][col] && 
                gameBoard[1][col] == gameBoard[2][col]) {
                return true
            }
        }
        
        // Check diagonals
        if (gameBoard[0][0] != ' ' && 
            gameBoard[0][0] == gameBoard[1][1] && 
            gameBoard[1][1] == gameBoard[2][2]) {
            return true
        }
        
        if (gameBoard[0][2] != ' ' && 
            gameBoard[0][2] == gameBoard[1][1] && 
            gameBoard[1][1] == gameBoard[2][0]) {
            return true
        }
        
        return false
    }
    
    private fun isBoardFull(): Boolean {
        for (row in 0..2) {
            for (col in 0..2) {
                if (gameBoard[row][col] == ' ') {
                    return false
                }
            }
        }
        return true
    }
}