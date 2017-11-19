/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.graph.factory;

import alviz.base.graph.BaseGraph;
import alviz.graph.Graph;
import alviz.util.Ellipse;
import java.util.Random;

/**
 *
 * @author baskaran
 */
public class Tree2DRectangle {

    private BaseGraph graph;

    private int screenClearance=10;

    private boolean debug;
    private int branchingFactor;
    private int width;
    private int height;
    private int nodeWidth;

    private int effWidth;
    private int effHeight;

    private int leafsAllowed;
    private int nodeClearance;

    private double majorAxis, minorAxis;
    private double xOffset, yOffset;

    private int nodeDim;

    private double delta_a;
    private double delta_b;

    private tree root;
    private int levels;

    public Tree2DRectangle(BaseGraph graph, int branchingfactor, int width, int height, int nodewidth, int nodeclearance, boolean debug) {

        this.graph = graph;
        this.debug = debug;
        this.branchingFactor = branchingfactor;
        this.width = width;
        this.height = height;
        this.nodeWidth = nodewidth;

        this.nodeClearance = nodeclearance;

        //width = 300;
        //height = 300;

        this.effWidth = width - 2*screenClearance;
        this.effHeight = height - 2*screenClearance;

        // center of the screen
        this.majorAxis = effWidth/2;
        this.minorAxis = effHeight/2;

        this.xOffset = this.width/2;
        this.yOffset = this.height/2;
        
        this.nodeDim = nodeWidth + nodeClearance;
        this.leafsAllowed = (int) ((4*majorAxis + 4*minorAxis) / nodeDim);

        this.delta_a = majorAxis/Math.log(leafsAllowed);
        this.delta_b = minorAxis/Math.log(leafsAllowed);

        this.root = null;
        this.levels = 0;
    }
    private void print() {
        System.out.printf("branchingFactor = %d\n", branchingFactor);
        System.out.printf("width = %d\n", width);
        System.out.printf("height = %d\n", height);
        System.out.printf("nodeWidth = %d\n", nodeWidth);
        System.out.printf("leafsAllowed = %d\n", leafsAllowed);
        System.out.printf("nodeClearance = %d\n", nodeClearance);
        System.out.printf("nodeDim = %d\n", nodeDim);
        System.out.printf("xOffset = %f\n", xOffset);
        System.out.printf("yOffset = %f\n", yOffset);
        System.out.printf("majorAxis = %f\n", majorAxis);
        System.out.printf("minorAxis = %f\n", minorAxis);
    }

    public BaseGraph create() {
        //print();
        
        root = maketree();
        BaseGraph g = buildGraph(root);
        tree.clear(root);

        //System.out.printf("graph nodes = %d, edges=%d\n", g.getNodeCount(), g.getEdgeCount());

        return g;
    }

    private tree maketree() {

        tree[] htree = new tree[leafsAllowed];
        {
            double dx = nodeDim;
            double dy = nodeDim;

            double x0 = majorAxis;
            double y0 = 0;

            double xdir = 0;
            double ydir = 1;

            double x = x0;
            double y = y0;
            
            tree hprev = null;
            for (int i=0; i<leafsAllowed; ++i) {
                
                tree t = new tree(0);
                t.setPoint(x, y);
                htree[i] = t;
                if (hprev != null) hprev.hnext = t;
                hprev = t;

                double prevx = x;
                double prevy = y;
                
                x += xdir * dx;
                y += ydir * dy;

                if (y > minorAxis) {
                    ydir = 0;
                    y = minorAxis;
                    xdir = -1;
                    x += xdir * (nodeDim - Math.abs(minorAxis - prevy)); //dx
                }
                else if (x < -majorAxis) {
                    xdir = 0;
                    x = -majorAxis;
                    ydir = -1;
                    y += ydir * (nodeDim - Math.abs(-majorAxis - prevx)); //dy;
                }
                else if (y < -minorAxis) {
                    ydir = 0;
                    y = -minorAxis;
                    xdir = 1;
                    x += xdir * (nodeDim - Math.abs(-minorAxis - prevy)); //dx;
                }
                else if (x > majorAxis) {
                    xdir = 0;
                    x = majorAxis;
                    ydir = 1;
                    y += ydir * (nodeDim - Math.abs(majorAxis - prevx)); //dy;
                }
                //System.out.printf("%d, (%f, %f)\n", i, t.x, t.y);

            }

            //System.out.printf("level=%d, a=%f, b=%f, nodes=%d\n", levels, majorAxis, minorAxis, leafsAllowed);
        }

        
        Random rand = new Random(System.nanoTime());
        int n = leafsAllowed;

        double prev_a = majorAxis;
        double prev_b = minorAxis;

        // do until root is reached...
        // i.e. number of nodes in the level is 1
        while (n > 1) {
            ++levels;

            int levelnodes = 0;
            int base=0;
            tree t=null, hprev=null;
            while (n > 0) {
                // fetch no of children
                int nchild = rand.nextInt(branchingFactor) + 1;
                if (nchild > n) nchild = n;

                // create parent and add children
                t = new tree(htree, base, nchild);
                htree[levelnodes] = t;
                base += nchild;
                n -= nchild;
                ++levelnodes;

                // assign level pointer and maintain prev tree
                if (hprev != null) hprev.hnext = t;
                hprev = t;
            }

            double perimeter = levelnodes * nodeDim;
            double axisratio = minorAxis/majorAxis;
            double a = perimeter / (4 * (1+axisratio));
            double b = axisratio * a;

            // reestimate the ellipse size based on nodes at the current level...
            this.delta_a = prev_a/Math.log(levelnodes)/2;
            this.delta_b = prev_b/Math.log(levelnodes)/2;

            // apply correction
            if (prev_a - a > delta_a) {
                a = prev_a - delta_a;
            }
            if (prev_b - b > delta_b) {
                b = prev_b - delta_b;
            }

            //System.out.printf("level=%d, a=%f, b=%f, nodes=%d\n", levels, a, b, levelnodes);

            // assign coordinates for the current level
            for (int i=0; i<levelnodes; ++i) {
                tree u = htree[i];
                tree c0, c1;
                int childCount = u.children.length;
                if (childCount%2 == 0) {
                    int k = childCount>>1;
                    c0 = u.children[k-1];
                    c1 = u.children[k];
                }
                else {
                    int k = childCount>>1;
                    c0 = u.children[k];
                    c1 = u.children[k];
                }
                computeParentCoordinate(c0.x, c0.y, c1.x, c1.y, a/prev_a, b/prev_b);
                u.setPoint(tmp_parent_x, tmp_parent_y);

                //System.out.printf("%d, (%f, %f)\n", i, u.x, u.y);
            }

            n = levelnodes;
            prev_a = a;
            prev_b = b;
            
            // collapse the level if the number of nodes is less than or equal to the branching factor...
            // this level will be the root level
            if (levelnodes <= branchingFactor || a <= branchingFactor*nodeDim || b <= branchingFactor*nodeDim  || a <= 5*nodeDim || b <= 5*nodeDim) {
                ++levels;
                // create parent and add children
                int nchild = levelnodes;
                levelnodes = 0;

                t = new tree(htree, 0, nchild);
                t.setPoint(0, 0);
                htree[levelnodes] = t;
                base += nchild;
                n -= nchild;
                ++levelnodes;
            }

            n = levelnodes;
        }

        return htree[0];
    }

    private double tmp_parent_x;
    private double tmp_parent_y;
    
    private void computeParentCoordinate(double x1, double y1, double x2, double y2, double xr, double yr) {

        double x = 0.5*(x1+x2);
        double y = 0.5*(y1+y2);

        x *= xr;
        y *= yr;

        tmp_parent_x = x;
        tmp_parent_y = y;
    }
    
    private BaseGraph buildGraph(tree t) {

        BaseGraph g = graph;
        int level=0;

        BaseGraph.Node n = g.createNode( (int)(-t.x + xOffset), (int)(t.y + yOffset));
        t.gnode = n;

        g.setStartNode(n);
        buildGraph(g, t, level+1);
        return g;
    }

    private void buildGraph(BaseGraph g, tree t, int level) {
        if (t != null) {
            if (!t.isLeaf()) {
                for (int i=0; i<t.children.length; ++i) {
                    tree c = t.children[i];
                    BaseGraph.Node n = g.createNode((int)(-c.x + xOffset), (int)(c.y + yOffset));
                    c.gnode = n;
                    g.createEdge(n, t.gnode);
                }
                for (int i=0; i<t.children.length; ++i) {
                    buildGraph(g, t.children[i], level+1);
                }
            }
            else {
                t.gnode.setLeaf();
            }
        }
    }

    static private class tree {
        
        public double x;
        public double y;
        public int descendents;
        public tree[] children;
        public tree hnext;
        public BaseGraph.Node gnode=null;

        public tree(int n) {
            x = 0;
            y = 0;
            descendents = 0;
            if (n > 0){
                children = new tree[n];
            }
            else {
                children = null;
            }
            hnext = null;
        }

        public tree(tree[] list, int base, int n) {
            this(n);
            for (int i=0; i<n; ++i, ++base) {
                children[i] = list[base];
                descendents += children[i].descendents;
            }
        }
        static public void clear(tree t) {
            if (t != null) {
                if (t.children != null) {
                    for (int i=0; i<t.children.length; ++i) {
                        clear(t.children[i]);
                        t.children[i] = null;
                    }
                    t.children = null;
                }
                t.hnext = null;
                t.gnode = null;
            }
        }

        public boolean isLeaf() { return children == null; }
        public void setPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public tree getFirstChild() {
            if (children != null) {
                return children[0];
            }
            return null;
        }

        public tree getLastChild() {
            if (children != null) {
                return children[children.length-1];
            }
            return null;
        }
    }
    
}
