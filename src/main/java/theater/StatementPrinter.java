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
     */
    public String statement() {
        final StringBuilder statement = new StringBuilder(
                "Statement for " + invoice.getCustomer() + System.lineSeparator());

        // loop responsible only for building the per-performance lines
        for (Performance performance : invoice.getPerformances()) {
            final Play play = getPlay(performance);
            statement.append(String.format("  %s: %s (%s seats)%n",
                    play.getName(),
                    usd(getAmount(performance)),
                    performance.getAudience()));
        }

        // totals now computed via query methods
        statement.append(String.format("Amount owed is %s%n", usd(getTotalAmount())));
        statement.append(
                String.format("You earned %s credits%n", getTotalVolumeCredits()));
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
     * @throws RuntimeException if the play type is unknown
     */
    private int getAmount(Performance performance) {
        final Play play = getPlay(performance);
        final int audience = performance.getAudience();
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

    /**
     * Calculates the volume credits for a single performance.
     *
     * @param performance the performance for which to calculate volume credits
     * @return the volume credits earned for this performance
     */
    private int getVolumeCredits(Performance performance) {
        int result = 0;
        final Play play = getPlay(performance);

        if ("tragedy".equals(play.getType())) {
            result += Math.max(
                    performance.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD,
                    0);
        }
        else if ("comedy".equals(play.getType())) {
            result += Math.max(
                    performance.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD,
                    0);
            result += performance.getAudience()
                    / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        else if ("history".equals(play.getType())) {
            result += Math.max(
                    performance.getAudience() - Constants.HISTORY_VOLUME_CREDIT_THRESHOLD,
                    0);
        }
        else if ("pastoral".equals(play.getType())) {
            result += Math.max(
                    performance.getAudience()
                            - Constants.PASTORAL_VOLUME_CREDIT_THRESHOLD,
                    0);
            result += performance.getAudience() / 2;
        }

        return result;
    }

    /**
     * Calculates the total volume credits across all performances.
     *
     * @return the total volume credits
     */
    private int getTotalVolumeCredits() {
        int result = 0;
        for (Performance performance : invoice.getPerformances()) {
            result += getVolumeCredits(performance);
        }
        return result;
    }

    /**
     * Calculates the total amount (in cents) across all performances.
     *
     * @return the total amount in cents
     */
    private int getTotalAmount() {
        int result = 0;
        for (Performance performance : invoice.getPerformances()) {
            result += getAmount(performance);
        }
        return result;
    }

    /**
     * Formats an amount (in cents) as a US currency string.
     *
     * @param amountInCents the amount in cents
     * @return the formatted amount as a US currency string
     */
    private String usd(int amountInCents) {
        final NumberFormat currencyFormatter =
                NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormatter.format(
                (double) amountInCents / Constants.PERCENT_FACTOR);
    }
}
