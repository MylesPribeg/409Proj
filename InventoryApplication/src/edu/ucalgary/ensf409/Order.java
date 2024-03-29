package edu.ucalgary.ensf409;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Order object responsible for making the hampers for each household and creating the best possible hamper for each household 
 * and also managing the inventory object that hold all the data of the database
 * @author sasha, myles, muhammad
 * @version 2.3
 * @since 1.0
 */

public class Order {
	
	private ArrayList<Household> houseHolds = new ArrayList<Household>();
	private FoodInv inventory;
	

	/**
	 * constructor with FoodInv object that sets it to the inventory field 
	 * @param inv = FoodInv object that is set to the invenory field
	 * @throws IllegalArgumentException throws if the inv object inputed is null
	 */
	public Order(FoodInv inv) throws IllegalArgumentException {
		if (inv == null) {
			throw new IllegalArgumentException();
		}
		else {
			this.inventory = inv;
		}
	}
	
	/**
	 * creates Order object with HouseHolds and a FoodInv object
	 * @param inv - FoodInv object that is set to the inventory field
	 * @param households - Household array that is set to the type Household ArrayList household field
	 * @throws IllegalArgumentException throws if the inv object inputed is null
	 */
	public Order(FoodInv inv, Household [] households) throws IllegalArgumentException { 
		
		if (inv == null) {
			throw new IllegalArgumentException();
		}
		else {
			this.inventory = inv;
		}
		
		for (Household i: households) {
			houseHolds.add(i);
	
		}
	}
	/**
	 * method responsible for starting the process of making hampers and also finalize the households hampers and possible food items
	 * @throws InsufficientInventoryException if there was not enough food in the database to populate one of the households hampers
	 */
	public void makeAndFinalizeOrder() throws InsufficientInventoryException {
		ArrayList<Food> copy = new ArrayList<Food>();
//		///creates copy of the food from the database in case food was deleted from the actual arraylist but there was an exception in which it was not possible to create a combination of food
		for (Food i: inventory.getFoodList()) {
			copy.add(i);
		}
		
		
		for(Household i: houseHolds) {//creates the hamper for each household
			if (!i.getClientList().isEmpty())
				makeHamper(i, copy);//method responsible for creating the hampers for the household
		}
		inventory.updateDB();// updates the inventory of the database if there were no problems with creating hampers for the household
	}
	/**
	 * adds HouseHold house to the Household Arraylist households
	 * @param house to be added to the Household ArrayList class field
	 */
	
	public void addHousehold(Household house) {
		
		houseHolds.add(house);
	}
	/**
	 * gets households arrayList
	 * @return the Household arrayList field households
	 */
	public ArrayList<Household> getHouseholds() {
		return this.houseHolds;
	}

	/**
	 * adds Household by index
	 * @param index - the index in teh arrayList house parameter is inserted
	 * @param house - the Household object to be inserted
	 */
	public void addHousehold(int index, Household house) {
		
		houseHolds.add(index, house);
	}
	/**
	 * gets household by index
	 * @param index in which the HouseHold object is got
	 * @return the Household Object selected by the index
	 */
	public Household getHousehold(int index) {
		if(index < 0 || index > houseHolds.size()) {
			return null;
		}
		return houseHolds.get(index);
	}
	/**
	 * removes household by index
	 * @param index that removes the houseHold
	 */
	public void removeHousehold(int index) {
		if(index < 0 || index > houseHolds.size()) {
			return;
		}
		houseHolds.remove(index);
	}
	/**
	 * getter that gets the foodInv object 
	 * @return the foodInv object 
	 */
	public FoodInv getInventory() {
	
		return this.inventory;
	}
	
	
	/**
	 * method that makes the hamper for each individual hamper - calls recursiveImplementationMakeHamperHelper to actually make the hampers
	 * @param house - current household
	 * @param copy - copy of the database items in case there is a need to rest
	 * @throws InsufficientInventoryException - if there is no possible combinations of items that satisy the clients needs
	 */
	
	private void makeHamper(Household house, ArrayList<Food> copy) throws InsufficientInventoryException {
		ArrayList<Food> inventoryList = inventory.getFoodList();//gets the food of the inventory
		
		Nutrition nutr = house.getTotalNeeds();//nutrtion of the house
		ArrayList<Food> foodList = new ArrayList<>();//creates temproary foodlist
		ArrayList<Food> bestFoodList = new ArrayList<>();//creates temporary bestFoodList
		
		int minimum = recursiveImplementationMakeHamperHelper(inventoryList,foodList,bestFoodList,nutr,0,Integer.MAX_VALUE); //calls the method that finds the best possible combination
		if(minimum == Integer.MAX_VALUE) {// case where there is no solution will return the max integer value
			this.inventory.setInv(copy);//restocks the inventory in case there were deleted food items in previous makeHamper calls
			//inventoryList = copy;// why is this here hasan????????
			//System.out.println(house.getName());
			throw new InsufficientInventoryException(house.getName());
			}
//		makeHamperHelper(inventoryList,temp);

		
		for (Food i: bestFoodList) {
			house.getHamper().addFood(i);
		}//creates hamper based on the best food list if no exception
		for (Food i: bestFoodList) {
			inventory.remove(i);
		}//removes the food items used to create the bestfoodlist from the inventory
		
		
	}
	/**
	 * the basic idea of the implementation is that it compares the current foodlist and its nutritional values to the households nutritional values
	 * if there is space for more food (ie all the requirements have not been met) it adds the food to the foodlist if the foodlist exceeds the
	 * nutrition values of the house it compares it calculates the excess calories in that certain foodlist combination and compares it to the current best combination
	 * if it is less then the new combination is replaced and if its not then its ignored. after this the foodlist item that was just added is removed to make room for future combinations
	 * the other case is that the added food to the foodlist does not exceed the nutrition values of the house in this case we recursivly call the method and keep track of all the variables
	 * but increment the current position in the loop (since in our implementation we go thorugh all the values/food items of the loop/database) this same procedure is then 
	 * performed on the shortened database size and is only complete when the best solution is found by going through all the cases or there is no possible combination 
	 * for food to feed the family inventory 
	 * 
	 * @param inventoryList - total list of all available food in the database 
	 * @param foodList	- current combination of food
	 * @param bestFoodList - the current best combination of food the meets the household requirements will minimizing calories
	 * @param nutrVals	- nutrition values of the household
	 * @param currentPosition - the current position of the recursive loop
	 * @param max - the excess number of calories for the best food combination
	 * @return the max parameter since its recirsove we need to store it
	 */

	private int recursiveImplementationMakeHamperHelper(ArrayList<Food> inventoryList, ArrayList<Food> foodList, ArrayList<Food> bestFoodList,Nutrition nutrVals,int currentPosition,int max) {	
		if(currentPosition > inventoryList.size()) {return Integer.MAX_VALUE;} //base case in case index goes larger then the max size
		for(int i =currentPosition; i < inventoryList.size(); i++) { //loops through the list starting at current position
			foodList.add(inventoryList.get(i));//adds the food at the certain index to the foodlist
			Nutrition temp =NutritionValuesOfFoodList(foodList);//calculates the nutritional value of the foodlist (creates temporary object)
			if(temp.getGrain() >= nutrVals.getGrain() && temp.getCalories() >= nutrVals.getCalories() && temp.getFruitsVeggies() >= nutrVals.getFruitsVeggies() && 
					temp.getOther() >= nutrVals.getOther() && temp.getProtein() >= nutrVals.getProtein()) {//compares the values of the food list to the household if the foodlist nutritional values is greater does this option
				int excess = (temp.getCalories() - nutrVals.getCalories()); //calculates the excess calories of the foodlist combination compared to the household
				if (excess < max) { //if the excess is less then the current max record the max and makes this foodlist the bestFoodList or the best possible combination
					max = excess;
					bestFoodList.clear();
					for(Food j: foodList) {
						bestFoodList.add(j);
					}
				}
			}else {// the other case in which the foodlist nutritional values is not greater then then the households nutrtional values in this case it calls the method recursivyl passing everything in the same but having the current
				int maxTemp = recursiveImplementationMakeHamperHelper( inventoryList, foodList, bestFoodList, nutrVals, i+1, max); //to ensure no items are repeated
				if(maxTemp < max) { max = maxTemp; }
			}
			foodList.remove(inventoryList.get(i)); //removes the item that was just inserted above. this case happens both for the recurisve and the compare steps
		}
		return max; //returns current max
	}
	
	
	/**
	 * private helper method used to calculate the nutritional values of the foodlist used in recursiveImplementationMakeHamperHelper
	 * returns an Nutrition object containing the values of the foodlist
	 * @param foodlist arrayList of food to create a nutrtion object with
	 * @return nutrition object that contains the nutritional values of the foodlist
	 */
	//private helper method used to calculate the nutritional values of the foodlist used in recursiveImplementationMakeHamperHelper
	//returns an Nutrition object containing the values of the foodlist
	private Nutrition NutritionValuesOfFoodList(ArrayList<Food> foodlist) {
		int grain = 0, fruitsVeggies =0, protein =0, other =0, calories =0;
		
		for(int i =0; i < foodlist.size(); i++) {
			grain+= foodlist.get(i).getNutritionValues().getGrain();
			fruitsVeggies+= foodlist.get(i).getNutritionValues().getFruitsVeggies();
			protein+= foodlist.get(i).getNutritionValues().getProtein();
			other+= foodlist.get(i).getNutritionValues().getOther();
			calories+= foodlist.get(i).getNutritionValues().getCalories();
		}
		return new Nutrition(grain, fruitsVeggies, protein, other, calories);
		
	}
}

