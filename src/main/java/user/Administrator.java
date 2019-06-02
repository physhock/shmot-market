package user;

import businesslogic.Deal;
import businesslogic.Order;
import storage.DealRepository;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Administrator extends User {

    public ArrayList<Deal> deals;

    public Administrator(String userName, String password) {
        super(userName, password);
        this.deals = findDeals();
    }

    private ArrayList<Deal> findDeals() {

        return DealRepository.getInstance().getDeals().stream()
                .filter(deal -> deal.getAdministrator().equals(this) && deal.getDealStatus().equals(Deal.DealStatus.OPEN))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void makeDecision(Deal deal, Deal.DealStatus dealStatus) {

        deal.setDealStatus(dealStatus);

        if (dealStatus.equals(Deal.DealStatus.APPROVED)) {
            // payment operations ( money transfer to the seller )
            // and deliver item to the buyer
            deal.getBuyer().addOrder(new Order(deal.getItem(), "123SPBRU"));
        }
    }

}
