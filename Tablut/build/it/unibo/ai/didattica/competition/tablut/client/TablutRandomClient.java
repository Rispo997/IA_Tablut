package it.unibo.ai.didattica.competition.tablut.client;

import java.awt.Point;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.exceptions.ActionException;
import it.unibo.ai.didattica.competition.tablut.exceptions.BoardException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ClimbingException;
import it.unibo.ai.didattica.competition.tablut.exceptions.DiagonalException;
import it.unibo.ai.didattica.competition.tablut.exceptions.OccupitedException;
import it.unibo.ai.didattica.competition.tablut.exceptions.PawnException;
import it.unibo.ai.didattica.competition.tablut.exceptions.StopException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ThroneException;

/**
 * 
 * @author A. Piretti, Andrea Galassi
 *
 */
public class TablutRandomClient extends TablutClient {

	private int game;

	public TablutRandomClient(String player, String name, int gameChosen) throws UnknownHostException, IOException {
		super(player, name);
		game = gameChosen;
	}

	public TablutRandomClient(String player) throws UnknownHostException, IOException {
		this(player, "random", 4);
	}

	public TablutRandomClient(String player, String name) throws UnknownHostException, IOException {
		this(player, name, 4);
	}

	public TablutRandomClient(String player, int gameChosen) throws UnknownHostException, IOException {
		this(player, "random", gameChosen);
	}

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		int gametype = 4;
		String role = "";
		String name = "random";
		// TODO: change the behavior?
		if (args.length < 1) {
			System.out.println("You must specify which player you are (WHITE or BLACK)");
			System.exit(-1);
		} else {
			System.out.println(args[0]);
			role = (args[0]);
		}
		if (args.length == 2) {
			System.out.println(args[1]);
			gametype = Integer.parseInt(args[1]);
		}
		if (args.length == 3) {
			name = args[2];
		}
		System.out.println("Selected client: " + args[0]);

		TablutRandomClient client = new TablutRandomClient(role, name, gametype);
		client.run();
	}
	
	/*
	 * Restituisce, per ogni pedina, la lista di mosse possibili.
	 * 
	 */
	private Map<Point, List<Point>> getPossibleMoves(List<int[]> pawns, State gameState, Game rules, Turn currentTurn)
			throws IOException {
		Map<Point, List<Point>> moves = new HashMap<Point, List<Point>>();

		for (int[] pawn : pawns) {
			List<Point> pawnMoves = new LinkedList<Point>();
			// Posizione di partenza della pedina
			String from = this.getCurrentState().getBox(pawn[0], pawn[1]);
			// Calcola tutte le mosse possibili sulla colonna della pedina
			for (int i = 0; i < gameState.getBoard().length; i++) {
				String to = this.getCurrentState().getBox(i, pawn[1]);

				if (this.checkMove(gameState, new Action(from, to, currentTurn))) {
					pawnMoves.add(new Point(i, pawn[1]));

				}
			}
			// Calcola tutte le mosse possibili sulla riga della pedina
			for (int j = 0; j < gameState.getBoard().length; j++) {
				String to = this.getCurrentState().getBox(pawn[0], j);

				if (this.checkMove(gameState, new Action(from, to, currentTurn))) {
					pawnMoves.add(new Point(pawn[0], j));
				}
			}

			moves.put(new Point(pawn[0], pawn[1]), pawnMoves);
		}
		for(Point move:moves.keySet()) {
			System.out.println("La pedina:" + move.toString() + "Può effettuare le seguenti mosse");
			System.out.println(moves.get(move));
		}
		return moves;

	}

	@Override
	public void run() {

		try {
			this.declareName();
		} catch (Exception e) {
			e.printStackTrace();
		}

		State state;

		Game rules = null;
		switch (this.game) {
		case 1:
			state = new StateTablut();
			rules = new GameTablut();
			break;
		case 2:
			state = new StateTablut();
			rules = new GameModernTablut();
			break;
		case 3:
			state = new StateBrandub();
			rules = new GameTablut();
			break;
		case 4:
			state = new StateTablut();
			state.setTurn(State.Turn.WHITE);
			rules = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
			System.out.println("Ashton Tablut game");
			break;
		default:
			System.out.println("Error in game selection");
			System.exit(4);
		}

		List<int[]> pawns = new ArrayList<int[]>();
		List<int[]> empty = new ArrayList<int[]>();

		System.out.println("You are player " + this.getPlayer().toString() + "!");

		while (true) {
			try {
				this.read();
			} catch (ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(1);
			}
			System.out.println("Current state:");
			state = this.getCurrentState();
			System.out.println(state.toString());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			if (this.getPlayer().equals(Turn.WHITE)) {
				// è il mio turno
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {
					int[] buf;
					for (int i = 0; i < state.getBoard().length; i++) {
						for (int j = 0; j < state.getBoard().length; j++) {
							if (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())
									|| state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
								buf = new int[2];
								buf[0] = i;
								buf[1] = j;
								pawns.add(buf);
							} else if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString())) {
								buf = new int[2];
								buf[0] = i;
								buf[1] = j;
								empty.add(buf);
							}
						}
					}

					int[] selected = null;
					try {
						getPossibleMoves(pawns, state, rules, Turn.WHITE);
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					boolean found = false;
					Action a = null;
					try {
						a = new Action("z0", "z0", State.Turn.WHITE);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					while (!found) {
						if (pawns.size() > 1) {
							selected = pawns.get(new Random().nextInt(pawns.size() - 1));
						} else {
							selected = pawns.get(0);
						}

						String from = this.getCurrentState().getBox(selected[0], selected[1]);

						selected = empty.get(new Random().nextInt(empty.size() - 1));
						String to = this.getCurrentState().getBox(selected[0], selected[1]);

						try {
							a = new Action(from, to, State.Turn.WHITE);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						try {
							rules.checkMove(state, a);
							found = true;
						} catch (Exception e) {

						}

					}

					System.out.println("Mossa scelta: " + a.toString());
					try {
						this.write(a);
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pawns.clear();
					empty.clear();

				}
				// è il turno dell'avversario
				else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
					System.out.println("Waiting for your opponent move... ");
				}
				// ho vinto
				else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				}
				// ho perso
				else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				}
				// pareggio
				else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}

			} else {

				// è il mio turno
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {
					int[] buf;
					for (int i = 0; i < state.getBoard().length; i++) {
						for (int j = 0; j < state.getBoard().length; j++) {
							if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
								buf = new int[2];
								buf[0] = i;
								buf[1] = j;
								pawns.add(buf);
							} else if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString())) {
								buf = new int[2];
								buf[0] = i;
								buf[1] = j;
								empty.add(buf);
							}
						}
					}

					int[] selected = null;

					boolean found = false;
					Action a = null;
					try {
						a = new Action("z0", "z0", State.Turn.BLACK);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					;
					while (!found) {
						selected = pawns.get(new Random().nextInt(pawns.size() - 1));
						String from = this.getCurrentState().getBox(selected[0], selected[1]);

						selected = empty.get(new Random().nextInt(empty.size() - 1));
						String to = this.getCurrentState().getBox(selected[0], selected[1]);

						try {
							a = new Action(from, to, State.Turn.BLACK);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						System.out.println("try: " + a.toString());
						try {
							rules.checkMove(state, a);
							found = true;
						} catch (Exception e) {

						}

					}

					System.out.println("Mossa scelta: " + a.toString());
					try {
						this.write(a);
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pawns.clear();
					empty.clear();

				}

				else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
					System.out.println("Waiting for your opponent move... ");
				} else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				} else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				} else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}

			}
		}

	}
	
	public Boolean checkMove(State state, Action a) 
	{
		//this.loggGame.fine(a.toString());
		//controllo la mossa
		if(a.getTo().length()!=2 || a.getFrom().length()!=2)
		{
			return false;
		}
		int columnFrom = a.getColumnFrom();
		int columnTo = a.getColumnTo();
		int rowFrom = a.getRowFrom();
		int rowTo = a.getRowTo();
		
		//controllo se sono fuori dal tabellone
		if(columnFrom>state.getBoard().length-1 || rowFrom>state.getBoard().length-1 || rowTo>state.getBoard().length-1 || columnTo>state.getBoard().length-1 || columnFrom<0 || rowFrom<0 || rowTo<0 || columnTo<0)
		{
			return false;		
		}
		
		//controllo che non vada sul trono
		if(state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.THRONE.toString()))
		{
			return false;
		}
		
		//controllo la casella di arrivo
		if(!state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.EMPTY.toString()))
		{
			return false;
		}
		
		//controllo se cerco di stare fermo
		if(rowFrom==rowTo && columnFrom==columnTo)
		{
			return false;
		}
		
		//controllo se sto muovendo una pedina giusta
		if(state.getTurn().equalsTurn(State.Turn.WHITE.toString()))
		{
			if(!state.getPawn(rowFrom, columnFrom).equalsPawn("W") && !state.getPawn(rowFrom, columnFrom).equalsPawn("K"))
			{
				return false;
			}
		}
		if(state.getTurn().equalsTurn(State.Turn.BLACK.toString()))
		{
			if(!state.getPawn(rowFrom, columnFrom).equalsPawn("B"))
			{
				return false;
			}
		}
		
		//controllo di non muovere in diagonale
		if(rowFrom != rowTo && columnFrom != columnTo)
		{
			return false;
		}
		
		//controllo di non scavalcare pedine
		if(rowFrom==rowTo)
		{
			if(columnFrom>columnTo)
			{
				for(int i=columnTo; i<columnFrom; i++)
				{
					if(!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString()))
					{
						return false;
					}
				}
			}
			else
			{
				for(int i=columnFrom+1; i<=columnTo; i++)
				{
					if(!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString()))
					{
						return false;
					}
				}
			}
		}
		else
		{
			if(rowFrom>rowTo)
			{
				for(int i=rowTo; i<rowFrom; i++)
				{
					if(!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString()) && !state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString()))
					{
						return false;
					}
				}
			}
			else
			{
				for(int i=rowFrom+1; i<=rowTo; i++)
				{
					if(!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString()) && !state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString()))
					{
						return false;
					}
				}
			}
		}
		
		
		return true;
	}
}
