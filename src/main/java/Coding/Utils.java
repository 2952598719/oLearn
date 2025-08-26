package Coding;

public class Utils {

    public static class ListNode {
        int val;
        ListNode next;
        public ListNode() {}
        public ListNode(int val) { this.val = val; }
    }

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        public TreeNode() {}
        public TreeNode(int val) { this.val = val; }
    }

    public static ListNode buildList(int[] nums) {
        ListNode virtual = new ListNode(), ptr = virtual;
        for (int num : nums) {
            ptr.next = new ListNode(num);
            ptr = ptr.next;
        }
        return virtual.next;
    }

}
