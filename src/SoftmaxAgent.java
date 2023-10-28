import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class SoftmaxAgent implements Agent {

  Random rand;
  double tau;
  double alpha;
  private Map<String, Integer> counts;
  private Map<String, Double> avgReward;
  private String lastChoice = "";

  public SoftmaxAgent(double tau, double alpha) {
    rand = new Random();
    this.tau = tau;
    this.alpha = alpha;
    counts = new HashMap<>();
    avgReward = new HashMap<>();
  }

   @Override
    public String present(String context, List<String> choices) {
      double draw = rand.nextDouble();
      double totalValue = 0.0;

      for (String choice : choices) {
        totalValue += Math.exp(avgReward.getOrDefault(choice, 0.0) / tau);
      }

      double totalWeight = 0.0;
      for (String choice : choices) {
        double choiceWeight = Math.exp(avgReward.getOrDefault(choice, 0.0) / tau) / totalValue;
        if (draw >= totalWeight 
            && draw <= totalWeight + choiceWeight) {
          lastChoice = choice;
        }
        totalWeight += choiceWeight;
      }
      return lastChoice;
    }

  @Override
  public void feedback(double score) {
    // update reward and count for last choice
    int oldCount = this.counts.getOrDefault(this.lastChoice, 0);
    double oldReward = this.avgReward.getOrDefault(this.lastChoice, 0.0);
    this.avgReward.put(this.lastChoice, (oldCount * oldReward + score) / (oldCount + 1));
    this.counts.put(this.lastChoice, oldCount + 1);

    if (tau > 1.0) {
      tau *= alpha;
    }
    else {
      tau = 1.0;
    }
  }
}