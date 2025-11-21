package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {

    private final Invoice invoice;
    private final Map<String, Play> plays;

    /**
     * Constructs a new {@code StatementPrinter}.
     *
     * @param invoice the invoice that this printer will use
     * @param plays   the mapping from play identifiers to plays
     */
    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     *
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    public String statement() {
        int totalAmount = 0;
        int volumeCredits = 0;

        StringBuilder result = new StringBuilder(
                "Statement for " + invoice.getCustomer() + System.lineSeparator());

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

        for (Performance performance : invoice.getPerformances()) {
            Play play = plays.get(performance.getPlayID());
            int audience = performance.getAudience();

            int thisAmount;
            switch (play.getType()) {
                case "tragedy":
                    // pricing
                    thisAmount = Constants.TRAGEDY_BASE_AMOUNT;
                    if (audience > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                        thisAmount += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                                * (audience - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                    }
                    // volume credits
                    volumeCredits += Math.max(
                            audience - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
                    break;

                case "comedy":
                    // pricing
                    thisAmount = Constants.COMEDY_BASE_AMOUNT;
                    if (audience > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                        thisAmount += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                                + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                                * (audience - Constants.COMEDY_AUDIENCE_THRESHOLD));
                    }
                    thisAmount += Constants.COMEDY_AMOUNT_PER_AUDIENCE * audience;
                    // volume credits
                    volumeCredits += Math.max(
                            audience - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
                    volumeCredits += audience / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
                    break;

                case "history":
                    // pricing (from README Task 5)
                    thisAmount = Constants.HISTORY_BASE_AMOUNT;
                    if (audience > Constants.HISTORY_AUDIENCE_THRESHOLD) {
                        thisAmount += Constants.HISTORY_OVER_BASE_CAPACITY_PER_PERSON
                                * (audience - Constants.HISTORY_AUDIENCE_THRESHOLD);
                    }
                    // volume credits (from README Task 5)
                    volumeCredits += Math.max(
                            audience - Constants.HISTORY_VOLUME_CREDIT_THRESHOLD, 0);
                    break;

                case "pastoral":
                    // pricing (from README Task 5)
                    thisAmount = Constants.PASTORAL_BASE_AMOUNT;
                    if (audience > Constants.PASTORAL_AUDIENCE_THRESHOLD) {
                        thisAmount += Constants.PASTORAL_OVER_BASE_CAPACITY_PER_PERSON
                                * (audience - Constants.PASTORAL_AUDIENCE_THRESHOLD);
                    }
                    // volume credits (from README Task 5)
                    volumeCredits += Math.max(
                            audience - Constants.PASTORAL_VOLUME_CREDIT_THRESHOLD, 0);
                    volumeCredits += audience / 2;
                    break;

                default:
                    throw new RuntimeException(
                            String.format("unknown type: %s", play.getType()));
            }

            // print line for this order
            result.append(String.format("  %s: %s (%s seats)%n",
                    play.getName(),
                    currencyFormatter.format(
                            (double) thisAmount / Constants.PERCENT_FACTOR),
                    audience));
            totalAmount += thisAmount;
        }

        result.append(String.format("Amount owed is %s%n",
                currencyFormatter.format(
                        (double) totalAmount / Constants.PERCENT_FACTOR)));
        result.append(String.format("You earned %s credits%n", volumeCredits));
        return result.toString();
    }
}