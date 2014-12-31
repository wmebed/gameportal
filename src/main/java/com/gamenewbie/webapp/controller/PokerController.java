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

    @RequestMapping(value = "main", method = RequestMethod.GET)
    public ModelAndView startGame(@RequestParam(required = false, value = "response") String response, HttpServletRequest request) throws Exception {
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
    	
    	request.getSession().setAttribute("deck", deck);
    	request.getSession().setAttribute("computer", computer);
    	request.getSession().setAttribute("hand", hand);
    	request.getSession().setAttribute("river", null);
    	
    	model.addAttribute("hand", hand.getCards());
    	model.addAttribute("computer", computer.getCards());
        model.addAttribute("state", 0);

        return new ModelAndView("poker/main", model.asMap());
    }
    
    @RequestMapping(value = "bet", method = RequestMethod.POST)
    public ModelAndView playGame(
    		HttpServletRequest request) throws Exception {
    	
    	boolean showCards = false;
    	HttpSession session = request.getSession();
    	Deck deck = (Deck) session.getAttribute("deck");
    	
    	Model model = new ExtendedModelMap();
    	Hand hand = (Hand) session.getAttribute("hand");
    	Hand computer = (Hand) session.getAttribute("computer");
    	Hand river = (Hand) session.getAttribute("river");
    	
    	Hand finalComputer = new Hand();
    	Hand finalHand = new Hand();
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
        }
    	
    	Double scoreComputer = null;
    	Double scoreHand = null;
    	if (showCards) {
    		scoreComputer = PokerGame.scoreHand(finalComputer);
    		scoreHand = PokerGame.scoreHand(finalHand);
    		if (scoreHand > scoreComputer) {
    			model.addAttribute("winner", "you");
    		} else if (scoreComputer > scoreHand) {
    			model.addAttribute("winner", "computer");
    		} else {
    			model.addAttribute("winner", "tie");
    		}
    	}
    	
    	model.addAttribute("river", river.getCards());
    	model.addAttribute("hand", hand.getCards());
    	model.addAttribute("computer", computer.getCards());
    	model.addAttribute("showCards", showCards);
        model.addAttribute("state", 1);
        model.addAttribute("scoreHand", scoreHand);
        model.addAttribute("scoreComputer", scoreComputer);

        return new ModelAndView("poker/main", model.asMap());
    }
}
