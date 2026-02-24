package attacks;

public class AttackResult {
    private final String candidate;
    private final String score;
    private final String preview;

    public AttackResult(String candidate, String score, String preview) {
        this.candidate = candidate;
        this.score = score;
        this.preview = preview;
    }

    public String getCandidate() {
        return candidate;
    }

    public String getScore() {
        return score;
    }

    public String getPreview() {
        return preview;
    }
}
