package message;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * After the requests of the user have been verified, we should create an object
 * of Order. Information: 1.SearchCar info 2.carID(the finally car that the user
 * choose) 3.userID 4.LinkedList of Ticket
 */
public class Order implements Serializable {

	// pull from
	private LinkedList<Car> info_carlist = null;

	private SearchCar info_ticket = null;

	private Car selected_car = null;

	// push item
	public LinkedList<Ticket> TicketList = new LinkedList<Ticket>();

	public LinkedList<Ticket> getOrderTicketList() {
		return TicketList;
	}

	public Car getSelected_car() {
		return this.selected_car;
	}

	public Order() {
	}

	/**
	 * if it have been without return trip;
	 * 
	 * @param Available
	 *            input_info_carlist
	 * @param String
	 *            input_carID
	 * @param String
	 *            input_userID
	 */
	public Order(Available input_info_carlist, String input_carID, String input_userID, SearchCar info_ticket) {
		// setting the pulling elements
		this.info_carlist = input_info_carlist.getAvailable();
		this.info_ticket = info_ticket;

		// Search the right carID from the carlist
		for (int i = 0; i < this.info_carlist.size(); i++) {
			if (input_carID.equals(info_carlist.get(i).getCarID())) {
				selected_car = info_carlist.get(i);
			}
		}

		if (selected_car == null) {
			// do nothing if carID is invalid
			System.out.println("invalid carID");
		}

		else {
			// pulling information from user input;
			String temp_carID = input_carID;
			String temp_userID = input_userID;

			// pulling information from selected_car
			String temp_depart = selected_car.getDepart();
			String temp_arrive = selected_car.getArrive();
			String temp_depart_time = info_ticket.getDepartDay() + "," + selected_car.getDepartTime();
			String temp_arrive_time = info_ticket.getDepartDay() + "," + selected_car.getArriveTime();
			String temp_early_discount = selected_car.getEarly_Discount();
			String temp_university_discount = selected_car.getUniversity_Discount();
			int temp_price = 0;

			// pulling information from SearchCar for type, number of tickets
			String temp_carriage = info_ticket.getCarriage();

			// setting price
			if (temp_carriage.equals("STANDARD")) {
				Price toGetPrice = new Price();
				temp_price = toGetPrice.getPrice(temp_depart, temp_arrive);
			} else if (temp_carriage.equals("BUSINESS")) {
				Price_of_Business toGetPrice = new Price_of_Business();
				temp_price = toGetPrice.getPrice(temp_depart, temp_arrive);
			}
			// setting Seat
			String temp_seat = info_ticket.getSeat();

			// different passenger Type
			for (int i = 0; i < info_ticket.getQuantity().length; i++) {

				// number of this type of passenger
				for (int j = 0; j < info_ticket.getQuantity()[i]; j++) {
					// pulling information from searchCar--info
					/**
					 * setting the price within this function 1.Normal: price* early bird discount
					 * 2.Child: price/2 3.Elder: price/2 4.Disable: price/2 5.Student: compare the
					 * early bird and university Discount
					 */
					if (i == 0) {
						double early_discount = 1.0;
						if (temp_early_discount == null) {

						} else if (temp_early_discount.equals("0.65")) {
							early_discount = 0.65;
						} else if (temp_early_discount.equals("0.8")) {
							early_discount = 0.8;
						} else if (temp_early_discount.equals("0.9")) {
							early_discount = 0.9;
						}

						Ticket temp_ticket = new Ticket(null, temp_carID, temp_userID, temp_depart, temp_arrive,
								temp_depart_time, temp_arrive_time, "NORMAL", temp_carriage, temp_early_discount,
								temp_university_discount, "unknow campartment", temp_seat,
								"" + temp_price * early_discount);

						TicketList.add(temp_ticket);
					}

					else if (i == 1) {
						Ticket temp_ticket = new Ticket(null, temp_carID, temp_userID, temp_depart, temp_arrive,
								temp_depart_time, temp_arrive_time, "CHILD", temp_carriage, temp_early_discount,
								temp_university_discount, "unknow campartment", temp_seat, "" + temp_price / 2);

						TicketList.add(temp_ticket);
					} else if (i == 2) {
						Ticket temp_ticket = new Ticket(null, temp_carID, temp_userID, temp_depart, temp_arrive,
								temp_depart_time, temp_arrive_time, "ELDER", temp_carriage, temp_early_discount,
								temp_university_discount, "unknow campartment", temp_seat, "" + temp_price / 2);

						TicketList.add(temp_ticket);
					} else if (i == 3) {
						Ticket temp_ticket = new Ticket(null, temp_carID, temp_userID, temp_depart, temp_arrive,
								temp_depart_time, temp_arrive_time, "DISABLE", temp_carriage, temp_early_discount,
								temp_university_discount, "unknow campartment", temp_seat, "" + temp_price / 2);

						TicketList.add(temp_ticket);
					} else if (i == 4) {
						double early_discount = 1.0;
						if (temp_early_discount == null) {

						} else if (temp_early_discount.equals("0.65")) {
							early_discount = 0.65;
						} else if (temp_early_discount.equals("0.8")) {
							early_discount = 0.8;
						} else if (temp_early_discount.equals("0.9")) {
							early_discount = 0.9;
						}
						double university_discount = 1.0;
						if (temp_university_discount == null) {

						} else if (temp_university_discount.equals("0.5")) {
							university_discount = 0.5;
						} else if (temp_university_discount.equals("0.7")) {
							early_discount = 0.7;
						} else if (temp_university_discount.equals("0.85")) {
							early_discount = 0.85;
						}

						double real_discount = Math.min(early_discount, university_discount);

						Ticket temp_ticket = new Ticket(null, temp_carID, temp_userID, temp_depart, temp_arrive,
								temp_depart_time, temp_arrive_time, "STUDENT", temp_carriage, temp_early_discount,
								temp_university_discount, "unknow campartment", temp_seat,
								"" + temp_price * real_discount);

						TicketList.add(temp_ticket);
					}

				}

			}
		}

	}

	public String toString() {
		String output = null;
		for (int i = 0; i < TicketList.size(); i++) {
			output = output + TicketList.get(i).toString();
		}
		return output;

	}

}