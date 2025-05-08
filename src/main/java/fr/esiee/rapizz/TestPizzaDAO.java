package fr.esiee.rapizz;

import fr.esiee.rapizz.dao.PizzaDAO;
import fr.esiee.rapizz.model.Pizza;
import fr.esiee.rapizz.model.Ingredient;

import java.util.List;

/**
 * Classe de test pour PizzaDAO
 */
public class TestPizzaDAO {
    
    public static void main(String[] args) {
        // Tester la méthode trouverTous()
        PizzaDAO pizzaDAO = new PizzaDAO();
        
        System.out.println("Test de récupération de toutes les pizzas :");
        List<Pizza> pizzas = pizzaDAO.trouverTous();
        System.out.println("Nombre de pizzas trouvées : " + pizzas.size());
        
        for (Pizza pizza : pizzas) {
            System.out.println("\nPizza : " + pizza.getNom());
            System.out.println("  Prix : " + pizza.getPrixBase() + "€");
            System.out.println("  Ingrédients :");
            
            List<Ingredient> ingredients = pizza.getIngredients();
            if (ingredients.isEmpty()) {
                System.out.println("    Aucun ingrédient trouvé");
            } else {
                for (Ingredient ingredient : ingredients) {
                    System.out.println("    - " + ingredient.getNom());
                }
            }
        }
    }
} 