package com.gamenewbie.webapp.controller;

import org.appfuse.Constants;
import org.appfuse.dao.SearchException;
import org.appfuse.service.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mebed.cards.Deck;
import com.mebed.cards.Hand;


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
@RequestMapping("/poker/main*")
public class PokerController {
    private UserManager userManager = null;

    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleRequest(@RequestParam(required = false, value = "response") String response) throws Exception {
        Model model = new ExtendedModelMap();

    	Deck deck = new Deck();
    	deck.shuffle();
    	Hand hand = new Hand();
    	for (int i = 0; i < 5; i++) {
    		hand.addCard(deck.dealCard());
    	}
    	
    	model.addAttribute("cards", hand.getCards());
        model.addAttribute("question", "What is the answer to the universe, and everything?");

        return new ModelAndView("poker/main", model.asMap());
    }
}
