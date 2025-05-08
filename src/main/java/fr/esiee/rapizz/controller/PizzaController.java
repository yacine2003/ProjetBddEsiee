package fr.esiee.rapizz.controller;

import fr.esiee.rapizz.dao.PizzaDAO;
import fr.esiee.rapizz.model.Pizza;

import java.util.List;

public class PizzaController {
    
    private final PizzaDAO pizzaDAO;

    public PizzaController() {
        this.pizzaDAO = new PizzaDAO();
    }

    public boolean ajouterPizza(Pizza pizza) {
        return pizzaDAO.inserer(pizza);
    }

    public boolean supprimerPizza(int idPizza){
        return pizzaDAO.supprimer(idPizza);

    }

    public boolean modifierPizza(Pizza pizza){
        return pizzaDAO.mettreAJour(pizza);

    }

    public List<Pizza> rechercherPizzaParNom(String terme){
        return pizzaDAO.rechercherParNom(terme);
    }

    public List<Pizza> listerToutesLesPizzas(){
        return pizzaDAO.trouverTous();
    }
    
    public Pizza retournerPizzaLaPlusVendue(){
        return pizzaDAO.trouverPizzaLaPlusVendue();
    }
    
}
