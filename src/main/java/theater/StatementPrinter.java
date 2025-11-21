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

        StringBuilder statement = new StringBuilder(
                "Statement for " + invoice.getCustomer() + System.lineSeparator());

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

        for (Performance performance : invoice.getPerformances()) {
            Play play = getPlay(performance);

            // add volume credits
            if ("tragedy".equals(play.getType())) {
                volumeCredits += Math.max(
                        performance.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
            } else if ("comedy".equals(play.getType())) {
                volumeCredits += Math.max(
                        performance.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
                volumeCredits += performance.getAudience()
                        / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
            } else if ("history".equals(play.getType())) {
                volumeCredits += Math.max(
                        performance.getAudience() - Constants.HISTORY_VOLUME_CREDIT_THRESHOLD, 0);
            } else if ("pastoral".equals(play.getType())) {
                volumeCredits += Math.max(
                        performance.getAudience() - Constants.PASTORAL_VOLUME_CREDIT_THRESHOLD, 0);
                volumeCredits += performance.getAudience() / 2;
            }

            // print line for this order
            statement.append(String.format("  %s: %s (%s seats)%n",
                    play.getName(),
                    currencyFormatter.format(
                            (double) getAmount(performance) / Constants.PERCENT_FACTOR),
                    performance.getAudience()));

            totalAmount += getAmount(performance);
        }

        statement.append(String.format("Amount owed is %s%n",
                currencyFormatter.format(
                        (double) totalAmount / Constants.PERCENT_FACTOR)));
        statement.append(String.format("You earned %s credits%n", volumeCredits));
        return statement.toString();
    }

    /**
     * Returns the play associated with the given performance.
     *
     * @param performance the performance
     * @return the play corresponding to that performance
     */
    private Play getPlay(Performance performance) {
        return plays.get(performance.getPlayID());
    }

    /**
     * Calculates the amount owed for a single performance.
     *
     * @param performance the performance for which to calculate the amount
     * @return the amount owed, in cents
     */
    private int getAmount(Performance performance) {
        Play play = getPlay(performance);
        int audience = performance.getAudience();
        int result;

        switch (play.getType()) {
            case "tragedy":
                result = Constants.TRAGEDY_BASE_AMOUNT;
                if (audience > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (audience - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;
            case "comedy":
                result = Constants.COMEDY_BASE_AMOUNT;
                if (audience > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (audience - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                result += Constants.COMEDY_AMOUNT_PER_AUDIENCE * audience;
                break;
            case "history":
                result = Constants.HISTORY_BASE_AMOUNT;
                if (audience > Constants.HISTORY_AUDIENCE_THRESHOLD) {
                    result += Constants.HISTORY_OVER_BASE_CAPACITY_PER_PERSON
                            * (audience - Constants.HISTORY_AUDIENCE_THRESHOLD);
                }
                break;
            case "pastoral":
                result = Constants.PASTORAL_BASE_AMOUNT;
                if (audience > Constants.PASTORAL_AUDIENCE_THRESHOLD) {
                    result += Constants.PASTORAL_OVER_BASE_CAPACITY_PER_PERSON
                            * (audience - Constants.PASTORAL_AUDIENCE_THRESHOLD);
                }
                break;
            default:
                throw new RuntimeException(
                        String.format("unknown type: %s", play.getType()));
        }

        return result;
    }
}