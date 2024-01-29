package url.shortener.Avocado.domain.url.util;


import org.springframework.beans.factory.annotation.Value;

public class SnowflakeIdGenerator {
    private static final long EPOCH = 1672531200000L; // 2023/01/1 in milli sec

    private static final long NODE_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_NODE_ID = ~(-1L << NODE_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + NODE_ID_BITS;
    private static final long NODE_ID_SHIFT = SEQUENCE_BITS;

    private final long nodeId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeIdGenerator(@Value("${snowflake.node-id}") long nodeId) {
        if (nodeId < 0 || nodeId > MAX_NODE_ID) {
            throw new IllegalArgumentException(String.format("노드 ID는 0과 %d 사이여야 합니다.", MAX_NODE_ID));
        }
        this.nodeId = nodeId;
    }

    public synchronized Long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("시스템 시계가 뒤로 가는 경우는 허용되지 않습니다.");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT) | (nodeId << NODE_ID_SHIFT) | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

}
