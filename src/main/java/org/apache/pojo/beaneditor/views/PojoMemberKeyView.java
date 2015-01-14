package org.apache.pojo.beaneditor.views;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position.Bias;
import javax.swing.text.View;

import org.apache.pojo.beaneditor.model.PBEDocument;
import org.apache.pojo.beaneditor.model.outline.PBEONode;

public class PojoMemberKeyView extends AbstractPojoBeanEditorView {
    public static final Image EXPANDED, FOLDED;
    private final short leftInset;
    private final PBEONode node;
    private final int stepNo;
    private boolean folded;

    public static BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        double scaleX = (double) width / imageWidth;
        double scaleY = (double) height / imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

        return bilinearScaleOp.filter(image, new BufferedImage(width, height, image.getType()));
    }

    static {
        try {
            EXPANDED = ImageIO.read(PojoMemberKeyView.class.getResourceAsStream("/images/expanded.png"));
            FOLDED = ImageIO.read(PojoMemberKeyView.class.getResourceAsStream("/images/folded.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PojoMemberKeyView(Element elem) {
        super(elem, X_AXIS);
        node = (PBEONode) getElement().getAttributes().getAttribute(PBEDocument.ATTRIB_NODE_OBJ);
        stepNo = (int) getElement().getAttributes().getAttribute(PBEDocument.ATTRIB_STEP_NO);
        leftInset = (short) ((stepNo * 10) /*+ EXPANDED.getWidth(null) + 5 /* gap */);
    }

    @Override
    protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
        super.layoutMinorAxis(targetSpan, axis, offsets, spans);
        offsets[0] = 0;
    }

    @Override
    public void paint(Graphics g, Shape allocation) {
        super.paint(g, allocation);
/*
        if (node.isLeaf()) {
            return;
        }
        Rectangle alloc = (Rectangle) allocation;
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(alloc.x + stepNo > 0 ? (10 * stepNo) : 0, alloc.y);
        g2d.drawImage(folded ? FOLDED : EXPANDED, 0, 0, null);
        g2d.translate(-(alloc.x + +stepNo > 0 ? (10 * stepNo) : 0), -alloc.y);
*/
    }

    private List<PojoMemberView> foldedViews = new ArrayList<PojoMemberView>();

    public void toggleFold() {
        folded = !folded;

        if (folded) {
            PojoMemberView pmv = (PojoMemberView) getParent();
            PojoBeanView pbv = (PojoBeanView) pmv.getParent();
            final int pmvIndex = pbv.getViewIndex(pmv.getStartOffset(), Bias.Forward);
            final int nextIndex = pmvIndex + 1;

            PojoMemberView nextMemberView = (PojoMemberView) pbv.getView(nextIndex);
            int nextMemberStep = (int) nextMemberView.getElement().getAttributes()
                    .getAttribute(PBEDocument.ATTRIB_STEP_NO);

            while (nextMemberStep > stepNo) {
                foldedViews.add(nextMemberView);
                pbv.remove(nextIndex);

                nextMemberView = (PojoMemberView) pbv.getView(nextIndex);
                nextMemberStep = (int) nextMemberView.getElement().getAttributes()
                        .getAttribute(PBEDocument.ATTRIB_STEP_NO);
            }
        } else {
            PojoMemberView pmv = (PojoMemberView) getParent();
            PojoBeanView pbv = (PojoBeanView) pmv.getParent();
            int pmvIndex = pbv.getViewIndex(pmv.getStartOffset(), Bias.Forward) + 1;

            PojoMemberView[] addedViews = new PojoMemberView[foldedViews.size()];
            foldedViews.toArray(addedViews);
            foldedViews.clear();

            for (PojoMemberView v : addedViews) {
                pbv.insert(pmvIndex++, v);
            }
        }
    }

    @Override
    public int getNextVisualPositionFrom(int pos, Bias b, Shape a, int direction, Bias[] biasRet)
            throws BadLocationException {
        int nextPos = super.getNextVisualPositionFrom(pos, b, a, direction, biasRet);
        if (folded) {
            PojoBeanView pbv = (PojoBeanView) getParent().getParent();
            int vi = pbv.getViewIndex(nextPos, b);
            View nextView = pbv.getView(vi);
            if (nextView == this) {
                return nextPos;
            }
            nextPos = nextView.getNextVisualPositionFrom(nextView.getStartOffset(), b, a, direction, biasRet);
        }

        return nextPos;
    }

    @Override
    protected int getViewIndexAtPosition(int pos) {
        return 0;
    }

    @Override
    protected short getLeftInset() {
        return leftInset;
    }

    public PBEONode getNode() {
        return node;
    }

    public int getStepNo() {
        return stepNo;
    }

    public boolean isFolded() {
        return folded;
    }
}
