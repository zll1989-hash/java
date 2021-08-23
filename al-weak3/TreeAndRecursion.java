package com.sankuai.inf.leaf.server.Algo.weak3;

/**
 * <p> Description:TreeAndRecursion </p>
 *
 * @Author Lucien
 * @create 2021/8/18 15:25
 */
public class TreeAndRecursion {

    private TreeNode ans;

    public TreeAndRecursion() {
        this.ans = null;
    }

    private boolean dfs(TreeNode root, TreeNode p, TreeNode q) {

        if (root == null) {
            return false;
        }

        boolean lSon = dfs(root.left, p, q);
        boolean rSon = dfs(root.right, p, q);
        boolean ancestorFlag = (lSon && rSon) || ((root.val == p.val || root.val == q.val) && (lSon || rSon));

        if (ancestorFlag) {
            ans = root;
        }

        return lSon || rSon || (root.val == p.val || root.val == q.val);
    }

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        this.dfs(root, p, q);
        return this.ans;
    }

}
