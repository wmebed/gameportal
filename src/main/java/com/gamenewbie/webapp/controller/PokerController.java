package com.gamenewbie.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.appfuse.Constants;
import org.appfuse.dao.SearchException;
import org.appfuse.service.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mebed.betting.Account;
import com.mebed.betting.BettingStrategy;
import com.mebed.betting.HandStatus;
import com.mebed.betting.Strategy;
import com.mebed.cards.AbstractCard;
import com.mebed.cards.Card;
import com.mebed.cards.Deck;
import com.mebed.cards.Hand;
import com.mebed.cards.PokerGame;


/**
 * Simple class to retrieve a list of users from the database.
 * <p/>
 * <p>
 * <a href="UserController.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
@Controller
@RequestMapping("/poker")
public class PokerController {
	

    private UserManager userManager = null;

    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView startGame(@RequestParam(required = false, value = "reset") boolean reset, HttpServletRequest request) throws Exception {
        Model model = new ExtendedModelMap();

    	Deck deck = new Deck();
    	deck.shuffle();
    	Hand hand = new Hand();
    	for (int i = 0; i < 2; i++) {
    		hand.addCard(deck.dealCard());
    	}
    	
    	Hand computer = new Hand();
    	for (int i = 0; i < 2; i++) {
    		computer.addCard(deck.dealCard());
    	}
    
    	Account playerAccount = (Account) request.getSession().getAttribute("playerAccount");;
    	Account computerAccount = (Account) request.getSession().getAttribute("computerAccount");
    	Integer dealerButton = (Integer) request.getSession().getAttribute("dealerButton");
    	HandStatus handStatus = HandStatus.start;

    	if (reset || playerAccount == null) {
    		playerAccount = new Account(100);
    		request.getSession().setAttribute("playerAccount", playerAccount);
    		computerAccount = new Account(100);
    		request.getSession().setAttribute("computerAccount", computerAccount);
    		dealerButton = 0;
    	}
    	
    	String message = null;
    	
    	
    	// Two player game
//    	if (dealerButton == 0) {
//    		dealerButton = 1;
//    	} else {
//    		dealerButton = 0;
//    	}
    	
    	if (dealerButton == 0) {
    		message = "Ante is 1, your bet";
    	} else {
    		message = "Computer bets, what do you do?";
    	}
    	Account pot = new Account(0);
    	computerAccount.withdraw(1);
    	playerAccount.withdraw(1);
    	pot.deposit(2);
    	
    	request.getSession().setAttribute("pot", pot);
    	request.getSession().setAttribute("deck", deck);
    	request.getSession().setAttribute("computer", computer);
    	request.getSession().setAttribute("hand", hand);
    	request.getSession().setAttribute("river", null);
    	request.getSession().setAttribute("dealerButton", dealerButton);
    	
    	
    	model.addAttribute("message", message);
    	model.addAttribute("computerAccount", computerAccount);
    	model.addAttribute("playerAccount", playerAccount);
    	model.addAttribute("pot", pot);
    	model.addAttribute("hand", hand.getCards());
    	model.addAttribute("computer", computer.getCards());
        model.addAttribute("handStatus", handStatus.toString());

        return new ModelAndView("poker/main", model.asMap());
    }
    
    @RequestMapping(value = "bet", method = RequestMethod.POST)
    public ModelAndView playGame(@RequestParam(required = false, value = "bet") Double bet, @RequestParam(required = false, value = "action") String action,
    		HttpServletRequest request) throws Exception {

    	Model model = new ExtendedModelMap();
    	String message = null;
    	boolean showCards = false;

    	if (bet == null) {
    		bet = 0.0;
    	}

    	Integer dealerButton = (Integer) request.getSession().getAttribute("dealerButton");
    	if (dealerButton == 0) {
    		message = "You last bet was " + bet + ", the computer called";
    	}
    	
    	HttpSession session = request.getSession();
    	Deck deck = (Deck) session.getAttribute("deck");  	
    	Hand hand = (Hand) session.getAttribute("hand");
    	Hand computer = (Hand) session.getAttribute("computer");
    	Hand river = (Hand) session.getAttribute("river");
    	Account pot = (Account) session.getAttribute("pot");
    	Account computerAccount = (Account) session.getAttribute("computerAccount");
    	Account playerAccount = (Account) session.getAttribute("playerAccount");
    	HandStatus handStatus = null;
    	
    	if (action.equals("fold")) {
    		fold(pot, computerAccount, message);
    		message = "You folded";
    		handStatus = HandStatus.finish;
    	} else {
    		if (action.equals(bet)) {
    			handStatus = HandStatus.raise;
    		} else if (action.equals("call")) {
    			handStatus = HandStatus.call;
    		} else if (action.equals("bet")) {
    			// Automatic computer call...
    			handStatus = HandStatus.call;
    		} else if (action.equals("check")) {
    			// Automatic computer call...
    			handStatus = HandStatus.check;
    		}
    		Strategy strategy = getBettingStrategy(pot, computerAccount, hand, river);
    		if (strategy.getHandStatus() != HandStatus.fold) {
    			pot = processAccountsForBet(handStatus, bet, session, pot, computerAccount, playerAccount);
    		} else {
    			message = "Computer folded";
    			handStatus = HandStatus.finish;
    			playerAccount.deposit(pot.getBalance());
    			pot.setBalance(0.0);
    		}
    	}
    	
    	// Play hand
    	
    	Hand finalComputer = new Hand();
    	Hand finalHand = new Hand();
    	if (handStatus.equals(HandStatus.check) || handStatus.equals(HandStatus.call)) {
	    	if (river == null) {
	    		river = new Hand();
	        	for (int i = 0; i < 3; i++) {
	        		river.addCard(deck.dealCard());
	        	} 
	        	session.setAttribute("river", river);
	    	} else if (river.getCards().size() < 5) {
	        		river.addCard(deck.dealCard());
	        } else {
	        	for (int i = 0; i < 2; i++) {
	        		finalComputer.addCard(computer.getCards().get(i));
	        		finalHand.addCard(hand.getCards().get(i));
	        	}
	        	for (int i = 0; i < 5; i++) {
	        		finalComputer.addCard(river.getCards().get(i));
	        		finalHand.addCard(river.getCards().get(i));
	        	}
	        	showCards = true;
	        	handStatus = HandStatus.finish;
	        }
    	}
    	

    	if (showCards) {
    		message = scoreHands(finalComputer, finalHand, model, pot, computerAccount, playerAccount);
    	} 
    	
    	model.addAttribute("message", message);
    	if (river != null) {
    		model.addAttribute("river", river.getCards());
    	}
    	model.addAttribute("hand", hand.getCards());
    	model.addAttribute("computer", computer.getCards());
    	model.addAttribute("showCards", showCards);
    	model.addAttribute("pot", pot);
        model.addAttribute("handStatus", handStatus.toString());


        return new ModelAndView("poker/main", model.asMap());
    }

	private Account processAccountsForBet(HandStatus handStatus, Double bet, HttpSession session,
			Account pot, Account computerAccount, Account playerAccount) {
		
		if (handStatus.equals(HandStatus.call) || handStatus.equals(HandStatus.raise)) {
			pot.deposit(bet*2);
	    	playerAccount.withdraw(bet);
	    	computerAccount.withdraw(bet);
		}
    	
    	
		return pot;
	}
	
	private void fold(Account pot, Account computerAccount, String message) {
    	computerAccount.deposit(pot.getBalance());
    	pot.setBalance(0);
    	message = "You folded";
	}
    
    private String scoreHands(Hand finalComputer, Hand finalHand, Model model, Account pot, Account computerAccount, Account playerAccount) {
    	Double scoreComputer = null;
    	Double scoreHand = null;
    	String message = null;
		scoreComputer = PokerGame.scoreHand(finalComputer);
		scoreHand = PokerGame.scoreHand(finalHand);
		if (scoreHand > scoreComputer) {
			message = "You were the winner";
			playerAccount.deposit(pot.getBalance());
		} else if (scoreComputer > scoreHand) {
			message = "The computer was the winner";
			computerAccount.deposit(pot.getBalance());
		} else {
			message = "It was a tie";
			playerAccount.deposit(pot.getBalance()/2);
		}
		pot.setBalance(0);

        model.addAttribute("state", 1);
        model.addAttribute("scoreHand", scoreHand);
        model.addAttribute("scoreComputer", scoreComputer);
        return message;
    }
    
    private Strategy getBettingStrategy(Account pot, Account computerAccount, Hand hand, Hand river) {
		Hand scoreHand = new Hand();
		for (Card card : hand.getCards()) {
    		scoreHand.addCard(card);
        }
		if (river != null) {
	    	for (Card card : river.getCards()) {
	    		scoreHand.addCard(card);;
	        }
		}
    	return BettingStrategy.getStrategy(scoreHand, computerAccount, pot);
		
    }
    
}
