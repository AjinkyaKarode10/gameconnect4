package com.pratilipi.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pratilipi.dto.RequestDTO;
import com.pratilipi.dto.ResponseDTO;


@CrossOrigin
@RestController
@RequestMapping("/game")
public class GameController {

	private static final int YELLOW_COLOR_USER = 1;
	
	private static final int RED_COLOR_USER = 2;
	
	private static  int GAME_ID = 0;
	
	private static  int YELLOW_USER_ID_COUNT = 0;
	
	private static  int RED_USER_ID_COUNT = 0;
	
	private static final String FILE_PATH="C:\\Users\\neebal\\Documents\\Ajinkya\\Personal";
	
	
	@RequestMapping("/start")
    public ResponseDTO gameStart() throws Exception {
    	
    	int[][] gameMatrix = new int[6][7];
    	for (int[] row : gameMatrix) 
            Arrays.fill(row, 0); 
    	YELLOW_USER_ID_COUNT=0;
    	RED_USER_ID_COUNT=0;
    	
    	
    	GAME_ID++;
    	StringBuilder builder = new StringBuilder();
    	for(int i = 0; i < gameMatrix.length; i++)//for each row
    	{
    	   for(int j = 0; j < gameMatrix[0].length; j++)//for each column
    	   {
    	      builder.append(gameMatrix[i][j]+"");//append to the output string
    	      if(j < gameMatrix[0].length - 1)//if this is not the last row element
    	         builder.append(",");//then add comma (if you don't like commas you can use spaces)
    	   }
    	   builder.append("\n");//append new line at the end of the row
    	}
    	File file = new File(FILE_PATH + File.separator +GAME_ID+".txt");
    	//file.mkdirs(); // If the directory containing the file and/or its parent(s) does not exist
    	System.out.println(file.createNewFile());
    	BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    	writer.write(builder.toString());//save the string representation of the board
    	writer.close();
		
    	
    	ResponseDTO responseDTO = new ResponseDTO();
    	responseDTO.setResponseMessage("READY");
    	responseDTO.setStatus("SUCCESS");
    	responseDTO.setGameId(GAME_ID);
    	
    	return responseDTO;
		
		
    }
	
	
	@PostMapping("/move")
    public ResponseDTO userMove(@RequestBody RequestDTO requestDTO) throws Exception {
    	
    	int[][] gameBoard = readFromFile(GAME_ID);
    	int currentPlayer = requestDTO.getUserID() == YELLOW_COLOR_USER ? YELLOW_COLOR_USER : RED_COLOR_USER;
    	ResponseDTO responseDTO = new ResponseDTO();
    	if(requestDTO.getUserID() !=1 && requestDTO.getUserID() !=2)
    	{
    		responseDTO.setStatus("INVALID");
    		responseDTO.setResponseMessage("Move is invalid , Userid invalid");
    		return responseDTO;
    	}
    	
    	if(requestDTO.getUserID() == YELLOW_COLOR_USER)
    	{
    		YELLOW_USER_ID_COUNT++;
    	}
    	if(requestDTO.getUserID() == RED_COLOR_USER)
    	{
    		RED_USER_ID_COUNT++;
    	}
    	
    	if(Math.abs(YELLOW_USER_ID_COUNT - RED_USER_ID_COUNT) > 1)
    	{
    		responseDTO.setStatus("INVALID");
    		responseDTO.setResponseMessage("Move is invalid , let other user play his/her turn");
    		return responseDTO;
    	}
    	
    	boolean response = updateGameBoard(gameBoard , requestDTO.getColumnNumber(),
    			  requestDTO.getUserID() == YELLOW_COLOR_USER ? YELLOW_COLOR_USER : RED_COLOR_USER);
    	
    	if(response)
    	{
    		writeToFile(gameBoard,GAME_ID);
    		
    		boolean IsWinner = checkWinner(gameBoard,currentPlayer);
    		
    		if(IsWinner)
    		{
    			responseDTO.setGameId(GAME_ID);
    			responseDTO.setResponseMessage("Winner "+currentPlayer);
    			return responseDTO;
    		}
    	}
    	
    	responseDTO.setResponseMessage("VALID");
    	
    	return responseDTO;
		
		
    }

	private boolean checkWinner(int[][] gameBoard, int i) {
		
		
		return checkVertical(gameBoard, i) || checkHorizontal(gameBoard, i) || checkDiagonal1(gameBoard,i);
		
	}


	private boolean updateGameBoard(int[][] gameBoard, int columnNumber, int color ) {
		boolean isBoardUpdate = false;
		for(int row = gameBoard.length-1 ; row >=0 ;row--)
		{
			if(gameBoard[row][columnNumber] == 0)
			{
				isBoardUpdate = true;
				gameBoard[row][columnNumber] = color;
				break;
			}
		}
		
		return isBoardUpdate;
	}


	private int[][] readFromFile(int gameId)
	{
		int[][] board = new int[6][7];
		BufferedReader reader;
		try 
		{
			reader = new BufferedReader(new FileReader(FILE_PATH + File.separator +gameId+".txt"));
			String line = "";
			int row = 0;
			while((line = reader.readLine()) != null)
			{
			   String[] cols = line.split(","); //note that if you have used space as separator you have to split on " "
			   int col = 0;
			   for(String  c : cols)
			   {
			      board[row][col] = Integer.parseInt(c);
			      col++;
			   }
			   row++;
			}
			
			reader.close();
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return board;
	}
	
	private void writeToFile(int [][]gameMatrix,int gameId)
	{
		try {
		File temp = new File(FILE_PATH + File.separator +gameId+".txt");
		if (temp.exists()) {
			PrintWriter writer = new PrintWriter(temp);
			writer.print("");
			writer.close();
			}
		
		StringBuilder builder = new StringBuilder();
    	for(int i = 0; i < gameMatrix.length; i++)//for each row
    	{
    	   for(int j = 0; j < gameMatrix.length; j++)//for each column
    	   {
    	      builder.append(gameMatrix[i][j]+"");//append to the output string
    	      if(j < gameMatrix.length - 1)//if this is not the last row element
    	         builder.append(",");//then add comma (if you don't like commas you can use spaces)
    	   }
    	   builder.append("\n");//append new line at the end of the row
    	}
    	BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH + File.separator +gameId+".txt" ));
    	writer.write(builder.toString());//save the string representation of the board
    	writer.close();
		
		
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	boolean checkVertical(int[][]board, int player){
		  for(int i = 0; i <= 6; ++i){//col
		    if (board[0][i] == player 
		        && board[1][i] == player
		        && board[2][i] == player
		        && board[3][i] == player
		        
		        		
		    ) return true;
		    
		    if (board[1][i] == player 
		        && board[2][i] == player
		        && board[3][i] == player
		        && board[4][i] == player
		    ) return true;
		    
		    if (board[2][i] == player 
			        && board[3][i] == player
			        && board[4][i] == player
			        && board[5][i] == player
			    ) return true;
			  }
		  
		  return false;
		  }
		  
		 

		boolean  checkHorizontal(int[][]board, int player){
		  for(int i = 0; i <= 5; ++i){
		    if (board[i][0] == player 
		        && board[i][1] == player
		        && board[i][2] == player
		        && board[i][3] == player
		    ) return true;
		    
		    if (board[i][1] == player 
		        && board[i][2] == player
		        && board[i][3] == player
		        && board[i][4] == player
		    ) return true;
		    
		    if (board[i][2] == player 
			        && board[i][3] == player
			        && board[i][4] == player
			        && board[i][5] == player
			    ) return true;
		    
		    if (board[i][3] == player 
			        && board[i][4] == player
			        && board[i][5] == player
			        && board[i][6] == player
			    ) return true;
		    
		   
		  }
		  return false;
		}

		private boolean checkDiagonal1(int[][]board, int player){
			//long
			for(int i=0,j=0;i<1;i++,j++)
			{
				if (board[i][j] == player 
				        && board[i+1][j+1] == player
				        && board[i+2][j+2] == player
				        && board[i+3][j+3] == player
				    ) return true;
				
				if ( board[i+1][j+1] == player
				        && board[i+2][j+2] == player
				        && board[i+3][j+3] == player
				        && board[i+4][j+4] == player 
				    ) return true;
				
				if ( 
				         board[i+2][j+2] == player
				        && board[i+3][j+3] == player
				        && board[i+4][j+4] == player 
				        && board[i+5][j+5] == player
				    ) return true;
			}
			
			//below
			for(int i=1,j=0;i<3;i++,j++)
			{
				if (board[i][j] == player 
				        && board[i+1][j+1] == player
				        && board[i+2][j+2] == player
				        && board[i+3][j+3] == player
				    ) return true;
			}
			//below
			for(int i=2,j=0;i<3;i++,j++)
			{
				if (board[i][j] == player 
				        && board[i+1][j+1] == player
				        && board[i+2][j+2] == player
				        && board[i+3][j+3] == player
				    ) return true;
			}
			
			
			//upper
			for(int i=0,j=1;i<3;i++,j++)
			{
				if (board[i][j] == player 
				        && board[i+1][j+1] == player
				        && board[i+2][j+2] == player
				        && board[i+3][j+3] == player
				    ) return true;
			}
			
			//upper
			for(int i=0,j=2;i<2;i++,j++)
			{
				if (board[i][j] == player 
				        && board[i+1][j+1] == player
				        && board[i+2][j+2] == player
				        && board[i+3][j+3] == player
				    ) return true;
			}
			
			//upper
			for(int i=0,j=3;i<1;i++,j++)
			{
				if (board[i][j] == player 
				        && board[i+1][j+1] == player
				        && board[i+2][j+2] == player
				        && board[i+3][j+3] == player
				    ) return true;
			}
		
		  
		  return false;
		}
//
//		function checkDiagonal2(field, player){
//		  // exercise for the reader
//		  return false;
//		}
//
//	
	
}
