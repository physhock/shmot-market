package user;

import businesslogic.*;
import services.AssignService;
import storage.AskRepository;
import storage.BetRepository;
import storage.DealRepository;

import java.util.ArrayList;
import java.util.Optional;

public class Buyer extends User {

    private ArrayList<Order> orders;

    public Buyer(String userName, String password) {
        super(userName, password);
        this.orders = new ArrayList<>();
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void makeBet(Item item, int bet) {

        Bet newBet = new Bet(this, item, bet);
        Optional<Ask> ask = checkForAsk(newBet);

        if (ask.isPresent()) {
            DealRepository.getInstance()
                    .placeDeal(AssignService.assignAdministratorToDeal(new Deal(ask.get(), newBet)));
            AskRepository.getInstance().removeAsk(ask.get());
        } else
            BetRepository.getInstance().placeBet(newBet);

    }

    public void closeOrder(String trackingId) {
        Order delivered = orders.stream().filter(order -> order.getTrackingId().equals(trackingId)).findFirst().get();
        orders.remove(delivered);
        DealRepository.getInstance().getDeals().stream()
                .filter(deal -> deal.getItem().equals(delivered.getItem()) && deal.getBuyer().equals(this))
                .findFirst().get().setDealStatus(Deal.DealStatus.CLOSED);
    }

    private Optional<Ask> checkForAsk(Bet bet) {

        return AskRepository.getInstance().getAsks()
                .stream().filter(x -> x.item.equals(bet.item) && x.ask == bet.bet)
                .findFirst();
    }

}
