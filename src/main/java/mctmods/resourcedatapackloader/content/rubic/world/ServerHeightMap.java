package mctmods.resourcedatapackloader.content.rubic.world;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IHeightMap;
import mctmods.resourcedatapackloader.util.Coords;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.annotation.Nonnull;

public class ServerHeightMap implements IHeightMap {
    private static final int NONE_SEGMENT = 0x7fffffff;
    @Nonnull private final int[] ymin;
    @Nonnull private final HeightMap ymax;
    @Nonnull private final int[][] segments;

    public ServerHeightMap(int[] heightmap) {
        this.ymin = new int[Cube.SIZE * Cube.SIZE];
        this.ymax = new HeightMap(heightmap);
        this.segments = new int[Cube.SIZE * Cube.SIZE][];
        for (int i = 0; i < Cube.SIZE * Cube.SIZE; i++) {
            this.ymin[i] = Coords.NO_HEIGHT;
            this.ymax.set(i, Coords.NO_HEIGHT);
        }
    }

    private static int getOpacity(int segmentIndex) { return (segmentIndex + 1) % 2; }

    private static int getLastSegmentIndex(int[] segments) {
        for (int i = segments.length - 1; i >= 0; i--) {
            if (segments[i] != NONE_SEGMENT) { return i; }
        }
        throw new Error("Invalid segments state");
    }

    private boolean parityCheck(int xzIndex) { return getLastSegmentIndex(segments[xzIndex]) % 2 == 0; }

    @Override public void onOpacityChange(int localX, int blockY, int localZ, int opacity) {
        if (blockY > Rubic.MAX_SUPPORTED_BLOCK_Y || blockY < Rubic.MIN_SUPPORTED_BLOCK_Y) { return; }
        int xzIndex = getIndex(localX, localZ);
        boolean isOpaque = opacity != 0;
        if (this.segments[xzIndex] == null) { this.setNoSegments(xzIndex, blockY, isOpaque); }
        else { this.setOpacityWithSegments(xzIndex, blockY, isOpaque); }
    }

    @Override public int getTopBlockY(int localX, int localZ) { return this.ymax.get(getIndex(localX, localZ)); }

    @Override public int getTopBlockYBelow(int localX, int localZ, int blockY) {
        int i = getIndex(localX, localZ);
        if (blockY > this.ymax.get(i)) { return this.getTopBlockY(localX, localZ); }
        if (blockY <= this.ymin[i]) { return Coords.NO_HEIGHT; }
        int[] segments = this.segments[i];
        if (segments == null) { return blockY - 1; }
        int mini = 0;
        int maxi = getLastSegmentIndex(segments);
        while (mini <= maxi) {
            int midi = (mini + maxi) >>> 1;
            int midPos = segments[midi];
            if (midPos < blockY) { mini = midi + 1; }
            else if (midPos > blockY) { maxi = midi - 1; }
            else {
                mini = midi + 1;
                break;
            }
        }
        assert (mini > 0) : String.format("can't find %d in %s", blockY, dump(localX, localZ));
        int segmentIndex = mini - 1;
        int blockYSegment = segments[segmentIndex];
        int blockYSegmentOpacity = getOpacity(segmentIndex);
        if (segmentIndex == 0) {
            assert blockYSegmentOpacity != 0 : "The bottom opacity segment is transparent!";
            return blockY - 1;
        }
        if (blockYSegmentOpacity == 0) { return blockYSegment - 1; }
        if (blockY != blockYSegment) { return blockY - 1; }
        int belowYSegment = segments[segmentIndex - 1];
        return belowYSegment - 1;
    }

    private void setNoSegments(int xzIndex, int blockY, boolean isOpaque) {
        if (isOpaque) { this.setNoSegmentsOpaque(xzIndex, blockY); }
        else { this.setNoSegmentsTransparent(xzIndex, blockY); }
    }

    private void setNoSegmentsOpaque(int xzIndex, int blockY) {
        if (this.ymin[xzIndex] == Coords.NO_HEIGHT && this.ymax.get(xzIndex) == Coords.NO_HEIGHT) {
            this.ymin[xzIndex] = blockY;
            this.ymax.set(xzIndex, blockY);
            return;
        }
        if (blockY == this.ymin[xzIndex] - 1) {
            this.ymin[xzIndex]--;
            return;
        }
        else if (blockY == this.ymax.get(xzIndex) + 1) {
            this.ymax.increment(xzIndex);
            return;
        }
        if (blockY > this.ymax.get(xzIndex) + 1) {
            this.segments[xzIndex] = new int[]{
                    this.ymin[xzIndex],
                    this.ymax.get(xzIndex) + 1,
                    blockY
            };
            this.ymax.set(xzIndex, blockY);
            return;
        }
        else if (blockY < this.ymin[xzIndex] - 1) {
            this.segments[xzIndex] = new int[]{
                    blockY,
                    blockY + 1,
                    this.ymin[xzIndex]
            };
            this.ymin[xzIndex] = blockY;
            return;
        }
        assert (blockY >= this.ymin[xzIndex] && blockY <= this.ymax.get(xzIndex));
    }

    private void setNoSegmentsTransparent(int xzIndex, int blockY) {
        if (this.ymin[xzIndex] == Coords.NO_HEIGHT && this.ymax.get(xzIndex) == Coords.NO_HEIGHT) { return; }
        assert !(this.ymin[xzIndex] == Coords.NO_HEIGHT || this.ymax.get(xzIndex) == Coords.NO_HEIGHT) :
                "Only one of ymin and ymax is NONE! This is not possible";
        if (this.ymax.get(xzIndex) == this.ymin[xzIndex]) {
            if (blockY == this.ymin[xzIndex]) {
                this.ymin[xzIndex] = Coords.NO_HEIGHT;
                this.ymax.set(xzIndex, Coords.NO_HEIGHT);
            }
            return;
        }
        if (blockY < this.ymin[xzIndex] || blockY > this.ymax.get(xzIndex)) { return; }
        if (blockY == this.ymin[xzIndex]) {
            this.ymin[xzIndex]++;
            return;
        }
        else if (blockY == this.ymax.get(xzIndex)) {
            this.ymax.decrement(xzIndex);
            return;
        }
        assert (blockY > this.ymin[xzIndex] && blockY <
                this.ymax.get(xzIndex)) :
                String.format("blockY outside of ymin/ymax range: %d -> [%d,%d]", blockY, this.ymin[xzIndex], this.ymax.get(xzIndex));
        this.segments[xzIndex] = new int[]{
                this.ymin[xzIndex],
                blockY,
                blockY + 1
        };
    }

    private void setOpacityWithSegments(int xzIndex, int blockY, boolean isOpaque) {
        int[] segments = this.segments[xzIndex];
        int minj = 0;
        int maxj = getLastSegmentIndex(segments);
        while (minj <= maxj) {
            int midj = (minj + maxj) >>> 1;
            int midPos = segments[midj];
            if (midPos < blockY) { minj = midj + 1; }
            else if (midPos > blockY) { maxj = midj - 1; }
            else {
                minj = midj + 1;
                break;
            }
        }
        int j = minj - 1;
        if (j < 0) { setOpacityWithSegmentsBelowBottom(xzIndex, blockY, isOpaque); }
        else if (blockY > this.ymax.get(xzIndex)) { setOpacityWithSegmentsAboveTop(xzIndex, blockY, isOpaque); }
        else { setOpacityWithSegmentsFor(xzIndex, blockY, j, isOpaque); }
    }

    private void setOpacityWithSegmentsBelowBottom(int xzIndex, int blockY, boolean isOpaque) {
        if (!isOpaque) { return; }
        boolean extendsBottomSegmentByOne = blockY == this.ymin[xzIndex] - 1;
        if (extendsBottomSegmentByOne) { moveSegmentStartDownAndUpdateMinY(xzIndex, 0); }
        else {
            int segment1 = blockY + 1;
            insertSegmentsBelow(xzIndex, 0, blockY, segment1);
            this.ymin[xzIndex] = blockY;
        }
    }

    private void setOpacityWithSegmentsAboveTop(int xzIndex, int blockY, boolean isOpaque) {
        if (!isOpaque) { return; }
        int[] segments = this.segments[xzIndex];
        int lastIndex = getLastSegmentIndex(segments);
        boolean extendsTopSegmentByOne = blockY == this.ymax.get(xzIndex) + 1;
        if (extendsTopSegmentByOne) { this.ymax.set(xzIndex, blockY); }
        else {
            int segmentPrevLastPlus1 = this.ymax.get(xzIndex) + 1;
            insertSegmentsBelow(xzIndex, lastIndex + 1, segmentPrevLastPlus1, blockY);
            this.ymax.set(xzIndex, blockY);
        }
    }

    private void setOpacityWithSegmentsFor(int xzIndex, int blockY, int segmentIndexWithBlockY, boolean isOpaque) {
        int[] segments = this.segments[xzIndex];
        int isOpaqueInt = isOpaque ? 1 : 0;
        int segmentWithBlockY = segments[segmentIndexWithBlockY];
        if (getOpacity(segmentIndexWithBlockY) == isOpaqueInt) { return; }
        int segmentTop = getSegmentTopBlockY(xzIndex, segmentIndexWithBlockY);
        if (segmentTop == segmentWithBlockY) {
            assert segmentWithBlockY == blockY;
            negateOneBlockSegment(xzIndex, segmentIndexWithBlockY);
            return;
        }
        int lastSegment = getLastSegmentIndex(segments);
        if (blockY == segmentTop) {
            if (segmentIndexWithBlockY == lastSegment) {
                this.ymax.decrement(xzIndex);
                return;
            }
            moveSegmentStartDownAndUpdateMinY(xzIndex, segmentIndexWithBlockY + 1);
            return;
        }
        if (blockY == segmentWithBlockY) {
            moveSegmentStartUpAndUpdateMinY(xzIndex, segmentIndexWithBlockY);
            return;
        }
        int newSegment2 = blockY + 1;
        insertSegmentsBelow(xzIndex, segmentIndexWithBlockY + 1, blockY, newSegment2);
    }

    private void negateOneBlockSegment(int xzIndex, int segmentIndexWithBlockY) {
        int[] segments = this.segments[xzIndex];
        int lastSegmentIndex = getLastSegmentIndex(segments);
        assert lastSegmentIndex >= 2 : "Less than 3 segments in array!";
        if (segmentIndexWithBlockY == lastSegmentIndex) {
            int segmentBelow = segments[segmentIndexWithBlockY - 1];
            this.ymax.set(xzIndex, segmentBelow - 1);
            if (segmentIndexWithBlockY == 2) {
                this.segments[xzIndex] = null;
                return;
            }
            segments[segmentIndexWithBlockY] = NONE_SEGMENT;
            segments[segmentIndexWithBlockY - 1] = NONE_SEGMENT;
            assert parityCheck(xzIndex) : "The number of segments was wrong!";
            return;
        }
        if (segmentIndexWithBlockY == 0) {
            this.ymin[xzIndex] = segments[2];
            if (lastSegmentIndex == 2) {
                this.segments[xzIndex] = null;
                return;
            }
            removeTwoSegments(xzIndex, 0);
            return;
        }
        removeTwoSegments(xzIndex, segmentIndexWithBlockY);
        if (lastSegmentIndex == 2) { this.segments[xzIndex] = null; }
    }

    private void moveSegmentStartUpAndUpdateMinY(int xzIndex, int segmentIndex) {
        this.segments[xzIndex][segmentIndex] = this.segments[xzIndex][segmentIndex] + 1;
        if (segmentIndex == 0) { this.ymin[xzIndex]++; }
    }

    private void moveSegmentStartDownAndUpdateMinY(int xzIndex, int segmentIndex) {
        this.segments[xzIndex][segmentIndex] = this.segments[xzIndex][segmentIndex] - 1;
        if (segmentIndex == 0) { this.ymin[xzIndex]--; }
    }

    private void removeTwoSegments(int xzIndex, int firstSegmentToRemove) {
        int[] segments = this.segments[xzIndex];
        int jmax = getLastSegmentIndex(segments);
        System.arraycopy(segments, firstSegmentToRemove + 2, segments, firstSegmentToRemove, jmax - 1 - firstSegmentToRemove);
        segments[jmax] = NONE_SEGMENT;
        segments[jmax - 1] = NONE_SEGMENT;
        assert parityCheck(xzIndex) : "The number of segments was wrong!";
        if (segments[0] == NONE_SEGMENT) { this.segments[xzIndex] = null; }
    }

    private void insertSegmentsBelow(int xzIndex, int theIndex, int... newSegments) {
        int lastIndex = getLastSegmentIndex(this.segments[xzIndex]);
        int expandSize = newSegments.length;
        if (this.segments[xzIndex].length >= lastIndex + expandSize) {
            System.arraycopy(this.segments[xzIndex], theIndex, this.segments[xzIndex], theIndex + expandSize, lastIndex + 1 - theIndex);
            System.arraycopy(newSegments, 0, this.segments[xzIndex], theIndex, expandSize);
        }
        else {
            int[] newSegmentArr = new int[(lastIndex + 1) + expandSize];
            int newArrIndex = 0;
            int oldArrIndex = 0;
            for (int i = 0; i < theIndex; i++) {
                newSegmentArr[newArrIndex] = this.segments[xzIndex][oldArrIndex];
                newArrIndex++;
                oldArrIndex++;
            }
            for (int newSegment : newSegments) {
                newSegmentArr[newArrIndex] = newSegment;
                newArrIndex++;
            }
            while (newArrIndex < newSegmentArr.length) {
                newSegmentArr[newArrIndex] = this.segments[xzIndex][oldArrIndex];
                newArrIndex++;
                oldArrIndex++;
            }
            this.segments[xzIndex] = newSegmentArr;
        }
        assert parityCheck(xzIndex) : "The number of segments was wrong!";
    }

    private int getSegmentTopBlockY(int xzIndex, int segmentIndex) {
        int[] segments = this.segments[xzIndex];
        if (segments.length - 1 == segmentIndex || segments[segmentIndex + 1] == NONE_SEGMENT) { return this.ymax.get(xzIndex); }
        return segments[segmentIndex + 1] - 1;
    }

    private static int getIndex(int localX, int localZ) { return (localZ << 4) | localX; }

    public byte[] getData() {
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(buf);
            writeData(out);
            out.close();
            return buf.toByteArray();
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

    public void readData(byte[] data) {
        int pos = 0;
        for (int i = 0; i < this.segments.length; i++) {
            this.ymin[i] = readIntBigEndian(data, pos);
            pos += Integer.BYTES;
            this.ymax.set(i, readIntBigEndian(data, pos));
            pos += Integer.BYTES;
            int[] segments = new int[readUShortBigEndian(data, pos)];
            pos += Short.BYTES;
            if (segments.length == 0) { continue; }
            for (int j = 0; j < segments.length; j++) {
                segments[j] = readIntBigEndian(data, pos);
                pos += Integer.BYTES;
            }
            this.segments[i] = segments;
            assert parityCheck(i) : "The number of segments was wrong!";
        }
    }

    private int readIntBigEndian(byte[] arr, int pos) {
        int ch1 = arr[pos] & 0xFF;
        int ch2 = arr[pos+1] & 0xFF;
        int ch3 = arr[pos+2] & 0xFF;
        int ch4 = arr[pos+3] & 0xFF;
        return (ch1 << 24) | (ch2 << 16) | (ch3 << 8) | ch4;
    }

    private int readUShortBigEndian(byte[] arr, int pos) {
        int ch1 = arr[pos] & 0xFF;
        int ch2 = arr[pos+1] & 0xFF;
        return (ch1 << 8) | ch2;
    }

    private void writeData(DataOutputStream out) throws IOException {
        for (int i = 0; i < this.segments.length; i++) {
            out.writeInt(this.ymin[i]);
            out.writeInt(this.ymax.get(i));
            int[] segments = this.segments[i];
            if (segments == null || segments.length == 0) { out.writeShort(0); }
            else {
                int lastSegmentIndex = getLastSegmentIndex(segments);
                out.writeShort(lastSegmentIndex + 1);
                for (int j = 0; j <= lastSegmentIndex; j++) { out.writeInt(segments[j]); }
            }
        }
    }

    public String dump(int localX, int localZ) {
        int i = getIndex(localX, localZ);
        StringBuilder buf = new StringBuilder();
        buf.append("range=[");
        buf.append(this.ymin[i]);
        buf.append(",");
        buf.append(this.ymax.get(i));
        buf.append("], segments(p,o)=");
        if (this.segments[i] != null) {
            for (int pos : this.segments[i]) {
                int opacity = getOpacity(i);
                buf.append("(");
                buf.append(pos);
                buf.append(",");
                buf.append(opacity);
                buf.append(")");
            }
        }
        return buf.toString();
    }
}
