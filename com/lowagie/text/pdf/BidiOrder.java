/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

public final class BidiOrder {
    private byte[] initialTypes;
    private byte[] embeddings;
    private byte paragraphEmbeddingLevel = -1;
    private int textLength;
    private byte[] resultTypes;
    private byte[] resultLevels;
    public static final byte L = 0;
    public static final byte LRE = 1;
    public static final byte LRO = 2;
    public static final byte R = 3;
    public static final byte AL = 4;
    public static final byte RLE = 5;
    public static final byte RLO = 6;
    public static final byte PDF = 7;
    public static final byte EN = 8;
    public static final byte ES = 9;
    public static final byte ET = 10;
    public static final byte AN = 11;
    public static final byte CS = 12;
    public static final byte NSM = 13;
    public static final byte BN = 14;
    public static final byte B = 15;
    public static final byte S = 16;
    public static final byte WS = 17;
    public static final byte ON = 18;
    public static final byte TYPE_MIN = 0;
    public static final byte TYPE_MAX = 18;
    private static final byte[] rtypes = new byte[65536];
    private static char[] baseTypes = new char[]{'\u0000', '\b', '\u000e', '\t', '\t', '\u0010', '\n', '\n', '\u000f', '\u000b', '\u000b', '\u0010', '\f', '\f', '\u0011', '\r', '\r', '\u000f', '\u000e', '\u001b', '\u000e', '\u001c', '\u001e', '\u000f', '\u001f', '\u001f', '\u0010', ' ', ' ', '\u0011', '!', '\"', '\u0012', '#', '%', '\n', '&', '*', '\u0012', '+', '+', '\n', ',', ',', '\f', '-', '-', '\n', '.', '.', '\f', '/', '/', '\t', '0', '9', '\b', ':', ':', '\f', ';', '@', '\u0012', 'A', 'Z', '\u0000', '[', '`', '\u0012', 'a', 'z', '\u0000', '{', '~', '\u0012', '', '\u0084', '\u000e', '\u0085', '\u0085', '\u000f', '\u0086', '\u009f', '\u000e', '\u00a0', '\u00a0', '\f', '\u00a1', '\u00a1', '\u0012', '\u00a2', '\u00a5', '\n', '\u00a6', '\u00a9', '\u0012', '\u00aa', '\u00aa', '\u0000', '\u00ab', '\u00af', '\u0012', '\u00b0', '\u00b1', '\n', '\u00b2', '\u00b3', '\b', '\u00b4', '\u00b4', '\u0012', '\u00b5', '\u00b5', '\u0000', '\u00b6', '\u00b8', '\u0012', '\u00b9', '\u00b9', '\b', '\u00ba', '\u00ba', '\u0000', '\u00bb', '\u00bf', '\u0012', '\u00c0', '\u00d6', '\u0000', '\u00d7', '\u00d7', '\u0012', '\u00d8', '\u00f6', '\u0000', '\u00f7', '\u00f7', '\u0012', '\u00f8', '\u02b8', '\u0000', '\u02b9', '\u02ba', '\u0012', '\u02bb', '\u02c1', '\u0000', '\u02c2', '\u02cf', '\u0012', '\u02d0', '\u02d1', '\u0000', '\u02d2', '\u02df', '\u0012', '\u02e0', '\u02e4', '\u0000', '\u02e5', '\u02ed', '\u0012', '\u02ee', '\u02ee', '\u0000', '\u02ef', '\u02ff', '\u0012', '\u0300', '\u0357', '\r', '\u0358', '\u035c', '\u0000', '\u035d', '\u036f', '\r', '\u0370', '\u0373', '\u0000', '\u0374', '\u0375', '\u0012', '\u0376', '\u037d', '\u0000', '\u037e', '\u037e', '\u0012', '\u037f', '\u0383', '\u0000', '\u0384', '\u0385', '\u0012', '\u0386', '\u0386', '\u0000', '\u0387', '\u0387', '\u0012', '\u0388', '\u03f5', '\u0000', '\u03f6', '\u03f6', '\u0012', '\u03f7', '\u0482', '\u0000', '\u0483', '\u0486', '\r', '\u0487', '\u0487', '\u0000', '\u0488', '\u0489', '\r', '\u048a', '\u0589', '\u0000', '\u058a', '\u058a', '\u0012', '\u058b', '\u0590', '\u0000', '\u0591', '\u05a1', '\r', '\u05a2', '\u05a2', '\u0000', '\u05a3', '\u05b9', '\r', '\u05ba', '\u05ba', '\u0000', '\u05bb', '\u05bd', '\r', '\u05be', '\u05be', '\u0003', '\u05bf', '\u05bf', '\r', '\u05c0', '\u05c0', '\u0003', '\u05c1', '\u05c2', '\r', '\u05c3', '\u05c3', '\u0003', '\u05c4', '\u05c4', '\r', '\u05c5', '\u05cf', '\u0000', '\u05d0', '\u05ea', '\u0003', '\u05eb', '\u05ef', '\u0000', '\u05f0', '\u05f4', '\u0003', '\u05f5', '\u05ff', '\u0000', '\u0600', '\u0603', '\u0004', '\u0604', '\u060b', '\u0000', '\u060c', '\u060c', '\f', '\u060d', '\u060d', '\u0004', '\u060e', '\u060f', '\u0012', '\u0610', '\u0615', '\r', '\u0616', '\u061a', '\u0000', '\u061b', '\u061b', '\u0004', '\u061c', '\u061e', '\u0000', '\u061f', '\u061f', '\u0004', '\u0620', '\u0620', '\u0000', '\u0621', '\u063a', '\u0004', '\u063b', '\u063f', '\u0000', '\u0640', '\u064a', '\u0004', '\u064b', '\u0658', '\r', '\u0659', '\u065f', '\u0000', '\u0660', '\u0669', '\u000b', '\u066a', '\u066a', '\n', '\u066b', '\u066c', '\u000b', '\u066d', '\u066f', '\u0004', '\u0670', '\u0670', '\r', '\u0671', '\u06d5', '\u0004', '\u06d6', '\u06dc', '\r', '\u06dd', '\u06dd', '\u0004', '\u06de', '\u06e4', '\r', '\u06e5', '\u06e6', '\u0004', '\u06e7', '\u06e8', '\r', '\u06e9', '\u06e9', '\u0012', '\u06ea', '\u06ed', '\r', '\u06ee', '\u06ef', '\u0004', '\u06f0', '\u06f9', '\b', '\u06fa', '\u070d', '\u0004', '\u070e', '\u070e', '\u0000', '\u070f', '\u070f', '\u000e', '\u0710', '\u0710', '\u0004', '\u0711', '\u0711', '\r', '\u0712', '\u072f', '\u0004', '\u0730', '\u074a', '\r', '\u074b', '\u074c', '\u0000', '\u074d', '\u074f', '\u0004', '\u0750', '\u077f', '\u0000', '\u0780', '\u07a5', '\u0004', '\u07a6', '\u07b0', '\r', '\u07b1', '\u07b1', '\u0004', '\u07b2', '\u0900', '\u0000', '\u0901', '\u0902', '\r', '\u0903', '\u093b', '\u0000', '\u093c', '\u093c', '\r', '\u093d', '\u0940', '\u0000', '\u0941', '\u0948', '\r', '\u0949', '\u094c', '\u0000', '\u094d', '\u094d', '\r', '\u094e', '\u0950', '\u0000', '\u0951', '\u0954', '\r', '\u0955', '\u0961', '\u0000', '\u0962', '\u0963', '\r', '\u0964', '\u0980', '\u0000', '\u0981', '\u0981', '\r', '\u0982', '\u09bb', '\u0000', '\u09bc', '\u09bc', '\r', '\u09bd', '\u09c0', '\u0000', '\u09c1', '\u09c4', '\r', '\u09c5', '\u09cc', '\u0000', '\u09cd', '\u09cd', '\r', '\u09ce', '\u09e1', '\u0000', '\u09e2', '\u09e3', '\r', '\u09e4', '\u09f1', '\u0000', '\u09f2', '\u09f3', '\n', '\u09f4', '\u0a00', '\u0000', '\u0a01', '\u0a02', '\r', '\u0a03', '\u0a3b', '\u0000', '\u0a3c', '\u0a3c', '\r', '\u0a3d', '\u0a40', '\u0000', '\u0a41', '\u0a42', '\r', '\u0a43', '\u0a46', '\u0000', '\u0a47', '\u0a48', '\r', '\u0a49', '\u0a4a', '\u0000', '\u0a4b', '\u0a4d', '\r', '\u0a4e', '\u0a6f', '\u0000', '\u0a70', '\u0a71', '\r', '\u0a72', '\u0a80', '\u0000', '\u0a81', '\u0a82', '\r', '\u0a83', '\u0abb', '\u0000', '\u0abc', '\u0abc', '\r', '\u0abd', '\u0ac0', '\u0000', '\u0ac1', '\u0ac5', '\r', '\u0ac6', '\u0ac6', '\u0000', '\u0ac7', '\u0ac8', '\r', '\u0ac9', '\u0acc', '\u0000', '\u0acd', '\u0acd', '\r', '\u0ace', '\u0ae1', '\u0000', '\u0ae2', '\u0ae3', '\r', '\u0ae4', '\u0af0', '\u0000', '\u0af1', '\u0af1', '\n', '\u0af2', '\u0b00', '\u0000', '\u0b01', '\u0b01', '\r', '\u0b02', '\u0b3b', '\u0000', '\u0b3c', '\u0b3c', '\r', '\u0b3d', '\u0b3e', '\u0000', '\u0b3f', '\u0b3f', '\r', '\u0b40', '\u0b40', '\u0000', '\u0b41', '\u0b43', '\r', '\u0b44', '\u0b4c', '\u0000', '\u0b4d', '\u0b4d', '\r', '\u0b4e', '\u0b55', '\u0000', '\u0b56', '\u0b56', '\r', '\u0b57', '\u0b81', '\u0000', '\u0b82', '\u0b82', '\r', '\u0b83', '\u0bbf', '\u0000', '\u0bc0', '\u0bc0', '\r', '\u0bc1', '\u0bcc', '\u0000', '\u0bcd', '\u0bcd', '\r', '\u0bce', '\u0bf2', '\u0000', '\u0bf3', '\u0bf8', '\u0012', '\u0bf9', '\u0bf9', '\n', '\u0bfa', '\u0bfa', '\u0012', '\u0bfb', '\u0c3d', '\u0000', '\u0c3e', '\u0c40', '\r', '\u0c41', '\u0c45', '\u0000', '\u0c46', '\u0c48', '\r', '\u0c49', '\u0c49', '\u0000', '\u0c4a', '\u0c4d', '\r', '\u0c4e', '\u0c54', '\u0000', '\u0c55', '\u0c56', '\r', '\u0c57', '\u0cbb', '\u0000', '\u0cbc', '\u0cbc', '\r', '\u0cbd', '\u0ccb', '\u0000', '\u0ccc', '\u0ccd', '\r', '\u0cce', '\u0d40', '\u0000', '\u0d41', '\u0d43', '\r', '\u0d44', '\u0d4c', '\u0000', '\u0d4d', '\u0d4d', '\r', '\u0d4e', '\u0dc9', '\u0000', '\u0dca', '\u0dca', '\r', '\u0dcb', '\u0dd1', '\u0000', '\u0dd2', '\u0dd4', '\r', '\u0dd5', '\u0dd5', '\u0000', '\u0dd6', '\u0dd6', '\r', '\u0dd7', '\u0e30', '\u0000', '\u0e31', '\u0e31', '\r', '\u0e32', '\u0e33', '\u0000', '\u0e34', '\u0e3a', '\r', '\u0e3b', '\u0e3e', '\u0000', '\u0e3f', '\u0e3f', '\n', '\u0e40', '\u0e46', '\u0000', '\u0e47', '\u0e4e', '\r', '\u0e4f', '\u0eb0', '\u0000', '\u0eb1', '\u0eb1', '\r', '\u0eb2', '\u0eb3', '\u0000', '\u0eb4', '\u0eb9', '\r', '\u0eba', '\u0eba', '\u0000', '\u0ebb', '\u0ebc', '\r', '\u0ebd', '\u0ec7', '\u0000', '\u0ec8', '\u0ecd', '\r', '\u0ece', '\u0f17', '\u0000', '\u0f18', '\u0f19', '\r', '\u0f1a', '\u0f34', '\u0000', '\u0f35', '\u0f35', '\r', '\u0f36', '\u0f36', '\u0000', '\u0f37', '\u0f37', '\r', '\u0f38', '\u0f38', '\u0000', '\u0f39', '\u0f39', '\r', '\u0f3a', '\u0f3d', '\u0012', '\u0f3e', '\u0f70', '\u0000', '\u0f71', '\u0f7e', '\r', '\u0f7f', '\u0f7f', '\u0000', '\u0f80', '\u0f84', '\r', '\u0f85', '\u0f85', '\u0000', '\u0f86', '\u0f87', '\r', '\u0f88', '\u0f8f', '\u0000', '\u0f90', '\u0f97', '\r', '\u0f98', '\u0f98', '\u0000', '\u0f99', '\u0fbc', '\r', '\u0fbd', '\u0fc5', '\u0000', '\u0fc6', '\u0fc6', '\r', '\u0fc7', '\u102c', '\u0000', '\u102d', '\u1030', '\r', '\u1031', '\u1031', '\u0000', '\u1032', '\u1032', '\r', '\u1033', '\u1035', '\u0000', '\u1036', '\u1037', '\r', '\u1038', '\u1038', '\u0000', '\u1039', '\u1039', '\r', '\u103a', '\u1057', '\u0000', '\u1058', '\u1059', '\r', '\u105a', '\u167f', '\u0000', '\u1680', '\u1680', '\u0011', '\u1681', '\u169a', '\u0000', '\u169b', '\u169c', '\u0012', '\u169d', '\u1711', '\u0000', '\u1712', '\u1714', '\r', '\u1715', '\u1731', '\u0000', '\u1732', '\u1734', '\r', '\u1735', '\u1751', '\u0000', '\u1752', '\u1753', '\r', '\u1754', '\u1771', '\u0000', '\u1772', '\u1773', '\r', '\u1774', '\u17b6', '\u0000', '\u17b7', '\u17bd', '\r', '\u17be', '\u17c5', '\u0000', '\u17c6', '\u17c6', '\r', '\u17c7', '\u17c8', '\u0000', '\u17c9', '\u17d3', '\r', '\u17d4', '\u17da', '\u0000', '\u17db', '\u17db', '\n', '\u17dc', '\u17dc', '\u0000', '\u17dd', '\u17dd', '\r', '\u17de', '\u17ef', '\u0000', '\u17f0', '\u17f9', '\u0012', '\u17fa', '\u17ff', '\u0000', '\u1800', '\u180a', '\u0012', '\u180b', '\u180d', '\r', '\u180e', '\u180e', '\u0011', '\u180f', '\u18a8', '\u0000', '\u18a9', '\u18a9', '\r', '\u18aa', '\u191f', '\u0000', '\u1920', '\u1922', '\r', '\u1923', '\u1926', '\u0000', '\u1927', '\u192b', '\r', '\u192c', '\u1931', '\u0000', '\u1932', '\u1932', '\r', '\u1933', '\u1938', '\u0000', '\u1939', '\u193b', '\r', '\u193c', '\u193f', '\u0000', '\u1940', '\u1940', '\u0012', '\u1941', '\u1943', '\u0000', '\u1944', '\u1945', '\u0012', '\u1946', '\u19df', '\u0000', '\u19e0', '\u19ff', '\u0012', '\u1a00', '\u1fbc', '\u0000', '\u1fbd', '\u1fbd', '\u0012', '\u1fbe', '\u1fbe', '\u0000', '\u1fbf', '\u1fc1', '\u0012', '\u1fc2', '\u1fcc', '\u0000', '\u1fcd', '\u1fcf', '\u0012', '\u1fd0', '\u1fdc', '\u0000', '\u1fdd', '\u1fdf', '\u0012', '\u1fe0', '\u1fec', '\u0000', '\u1fed', '\u1fef', '\u0012', '\u1ff0', '\u1ffc', '\u0000', '\u1ffd', '\u1ffe', '\u0012', '\u1fff', '\u1fff', '\u0000', '\u2000', '\u200a', '\u0011', '\u200b', '\u200d', '\u000e', '\u200e', '\u200e', '\u0000', '\u200f', '\u200f', '\u0003', '\u2010', '\u2027', '\u0012', '\u2028', '\u2028', '\u0011', '\u2029', '\u2029', '\u000f', '\u202a', '\u202a', '\u0001', '\u202b', '\u202b', '\u0005', '\u202c', '\u202c', '\u0007', '\u202d', '\u202d', '\u0002', '\u202e', '\u202e', '\u0006', '\u202f', '\u202f', '\u0011', '\u2030', '\u2034', '\n', '\u2035', '\u2054', '\u0012', '\u2055', '\u2056', '\u0000', '\u2057', '\u2057', '\u0012', '\u2058', '\u205e', '\u0000', '\u205f', '\u205f', '\u0011', '\u2060', '\u2063', '\u000e', '\u2064', '\u2069', '\u0000', '\u206a', '\u206f', '\u000e', '\u2070', '\u2070', '\b', '\u2071', '\u2073', '\u0000', '\u2074', '\u2079', '\b', '\u207a', '\u207b', '\n', '\u207c', '\u207e', '\u0012', '\u207f', '\u207f', '\u0000', '\u2080', '\u2089', '\b', '\u208a', '\u208b', '\n', '\u208c', '\u208e', '\u0012', '\u208f', '\u209f', '\u0000', '\u20a0', '\u20b1', '\n', '\u20b2', '\u20cf', '\u0000', '\u20d0', '\u20ea', '\r', '\u20eb', '\u20ff', '\u0000', '\u2100', '\u2101', '\u0012', '\u2102', '\u2102', '\u0000', '\u2103', '\u2106', '\u0012', '\u2107', '\u2107', '\u0000', '\u2108', '\u2109', '\u0012', '\u210a', '\u2113', '\u0000', '\u2114', '\u2114', '\u0012', '\u2115', '\u2115', '\u0000', '\u2116', '\u2118', '\u0012', '\u2119', '\u211d', '\u0000', '\u211e', '\u2123', '\u0012', '\u2124', '\u2124', '\u0000', '\u2125', '\u2125', '\u0012', '\u2126', '\u2126', '\u0000', '\u2127', '\u2127', '\u0012', '\u2128', '\u2128', '\u0000', '\u2129', '\u2129', '\u0012', '\u212a', '\u212d', '\u0000', '\u212e', '\u212e', '\n', '\u212f', '\u2131', '\u0000', '\u2132', '\u2132', '\u0012', '\u2133', '\u2139', '\u0000', '\u213a', '\u213b', '\u0012', '\u213c', '\u213f', '\u0000', '\u2140', '\u2144', '\u0012', '\u2145', '\u2149', '\u0000', '\u214a', '\u214b', '\u0012', '\u214c', '\u2152', '\u0000', '\u2153', '\u215f', '\u0012', '\u2160', '\u218f', '\u0000', '\u2190', '\u2211', '\u0012', '\u2212', '\u2213', '\n', '\u2214', '\u2335', '\u0012', '\u2336', '\u237a', '\u0000', '\u237b', '\u2394', '\u0012', '\u2395', '\u2395', '\u0000', '\u2396', '\u23d0', '\u0012', '\u23d1', '\u23ff', '\u0000', '\u2400', '\u2426', '\u0012', '\u2427', '\u243f', '\u0000', '\u2440', '\u244a', '\u0012', '\u244b', '\u245f', '\u0000', '\u2460', '\u249b', '\b', '\u249c', '\u24e9', '\u0000', '\u24ea', '\u24ea', '\b', '\u24eb', '\u2617', '\u0012', '\u2618', '\u2618', '\u0000', '\u2619', '\u267d', '\u0012', '\u267e', '\u267f', '\u0000', '\u2680', '\u2691', '\u0012', '\u2692', '\u269f', '\u0000', '\u26a0', '\u26a1', '\u0012', '\u26a2', '\u2700', '\u0000', '\u2701', '\u2704', '\u0012', '\u2705', '\u2705', '\u0000', '\u2706', '\u2709', '\u0012', '\u270a', '\u270b', '\u0000', '\u270c', '\u2727', '\u0012', '\u2728', '\u2728', '\u0000', '\u2729', '\u274b', '\u0012', '\u274c', '\u274c', '\u0000', '\u274d', '\u274d', '\u0012', '\u274e', '\u274e', '\u0000', '\u274f', '\u2752', '\u0012', '\u2753', '\u2755', '\u0000', '\u2756', '\u2756', '\u0012', '\u2757', '\u2757', '\u0000', '\u2758', '\u275e', '\u0012', '\u275f', '\u2760', '\u0000', '\u2761', '\u2794', '\u0012', '\u2795', '\u2797', '\u0000', '\u2798', '\u27af', '\u0012', '\u27b0', '\u27b0', '\u0000', '\u27b1', '\u27be', '\u0012', '\u27bf', '\u27cf', '\u0000', '\u27d0', '\u27eb', '\u0012', '\u27ec', '\u27ef', '\u0000', '\u27f0', '\u2b0d', '\u0012', '\u2b0e', '\u2e7f', '\u0000', '\u2e80', '\u2e99', '\u0012', '\u2e9a', '\u2e9a', '\u0000', '\u2e9b', '\u2ef3', '\u0012', '\u2ef4', '\u2eff', '\u0000', '\u2f00', '\u2fd5', '\u0012', '\u2fd6', '\u2fef', '\u0000', '\u2ff0', '\u2ffb', '\u0012', '\u2ffc', '\u2fff', '\u0000', '\u3000', '\u3000', '\u0011', '\u3001', '\u3004', '\u0012', '\u3005', '\u3007', '\u0000', '\u3008', '\u3020', '\u0012', '\u3021', '\u3029', '\u0000', '\u302a', '\u302f', '\r', '\u3030', '\u3030', '\u0012', '\u3031', '\u3035', '\u0000', '\u3036', '\u3037', '\u0012', '\u3038', '\u303c', '\u0000', '\u303d', '\u303f', '\u0012', '\u3040', '\u3098', '\u0000', '\u3099', '\u309a', '\r', '\u309b', '\u309c', '\u0012', '\u309d', '\u309f', '\u0000', '\u30a0', '\u30a0', '\u0012', '\u30a1', '\u30fa', '\u0000', '\u30fb', '\u30fb', '\u0012', '\u30fc', '\u321c', '\u0000', '\u321d', '\u321e', '\u0012', '\u321f', '\u324f', '\u0000', '\u3250', '\u325f', '\u0012', '\u3260', '\u327b', '\u0000', '\u327c', '\u327d', '\u0012', '\u327e', '\u32b0', '\u0000', '\u32b1', '\u32bf', '\u0012', '\u32c0', '\u32cb', '\u0000', '\u32cc', '\u32cf', '\u0012', '\u32d0', '\u3376', '\u0000', '\u3377', '\u337a', '\u0012', '\u337b', '\u33dd', '\u0000', '\u33de', '\u33df', '\u0012', '\u33e0', '\u33fe', '\u0000', '\u33ff', '\u33ff', '\u0012', '\u3400', '\u4dbf', '\u0000', '\u4dc0', '\u4dff', '\u0012', '\u4e00', '\ua48f', '\u0000', '\ua490', '\ua4c6', '\u0012', '\ua4c7', '\ufb1c', '\u0000', '\ufb1d', '\ufb1d', '\u0003', '\ufb1e', '\ufb1e', '\r', '\ufb1f', '\ufb28', '\u0003', '\ufb29', '\ufb29', '\n', '\ufb2a', '\ufb36', '\u0003', '\ufb37', '\ufb37', '\u0000', '\ufb38', '\ufb3c', '\u0003', '\ufb3d', '\ufb3d', '\u0000', '\ufb3e', '\ufb3e', '\u0003', '\ufb3f', '\ufb3f', '\u0000', '\ufb40', '\ufb41', '\u0003', '\ufb42', '\ufb42', '\u0000', '\ufb43', '\ufb44', '\u0003', '\ufb45', '\ufb45', '\u0000', '\ufb46', '\ufb4f', '\u0003', '\ufb50', '\ufbb1', '\u0004', '\ufbb2', '\ufbd2', '\u0000', '\ufbd3', '\ufd3d', '\u0004', '\ufd3e', '\ufd3f', '\u0012', '\ufd40', '\ufd4f', '\u0000', '\ufd50', '\ufd8f', '\u0004', '\ufd90', '\ufd91', '\u0000', '\ufd92', '\ufdc7', '\u0004', '\ufdc8', '\ufdef', '\u0000', '\ufdf0', '\ufdfc', '\u0004', '\ufdfd', '\ufdfd', '\u0012', '\ufdfe', '\ufdff', '\u0000', '\ufe00', '\ufe0f', '\r', '\ufe10', '\ufe1f', '\u0000', '\ufe20', '\ufe23', '\r', '\ufe24', '\ufe2f', '\u0000', '\ufe30', '\ufe4f', '\u0012', '\ufe50', '\ufe50', '\f', '\ufe51', '\ufe51', '\u0012', '\ufe52', '\ufe52', '\f', '\ufe53', '\ufe53', '\u0000', '\ufe54', '\ufe54', '\u0012', '\ufe55', '\ufe55', '\f', '\ufe56', '\ufe5e', '\u0012', '\ufe5f', '\ufe5f', '\n', '\ufe60', '\ufe61', '\u0012', '\ufe62', '\ufe63', '\n', '\ufe64', '\ufe66', '\u0012', '\ufe67', '\ufe67', '\u0000', '\ufe68', '\ufe68', '\u0012', '\ufe69', '\ufe6a', '\n', '\ufe6b', '\ufe6b', '\u0012', '\ufe6c', '\ufe6f', '\u0000', '\ufe70', '\ufe74', '\u0004', '\ufe75', '\ufe75', '\u0000', '\ufe76', '\ufefc', '\u0004', '\ufefd', '\ufefe', '\u0000', '\ufeff', '\ufeff', '\u000e', '\uff00', '\uff00', '\u0000', '\uff01', '\uff02', '\u0012', '\uff03', '\uff05', '\n', '\uff06', '\uff0a', '\u0012', '\uff0b', '\uff0b', '\n', '\uff0c', '\uff0c', '\f', '\uff0d', '\uff0d', '\n', '\uff0e', '\uff0e', '\f', '\uff0f', '\uff0f', '\t', '\uff10', '\uff19', '\b', '\uff1a', '\uff1a', '\f', '\uff1b', '\uff20', '\u0012', '\uff21', '\uff3a', '\u0000', '\uff3b', '\uff40', '\u0012', '\uff41', '\uff5a', '\u0000', '\uff5b', '\uff65', '\u0012', '\uff66', '\uffdf', '\u0000', '\uffe0', '\uffe1', '\n', '\uffe2', '\uffe4', '\u0012', '\uffe5', '\uffe6', '\n', '\uffe7', '\uffe7', '\u0000', '\uffe8', '\uffee', '\u0012', '\uffef', '\ufff8', '\u0000', '\ufff9', '\ufffb', '\u000e', '\ufffc', '\ufffd', '\u0012', '\ufffe', '\uffff', '\u0000'};

    public BidiOrder(byte[] arrby) {
        BidiOrder.validateTypes(arrby);
        this.initialTypes = (byte[])arrby.clone();
        this.runAlgorithm();
    }

    public BidiOrder(byte[] arrby, byte by) {
        BidiOrder.validateTypes(arrby);
        BidiOrder.validateParagraphEmbeddingLevel(by);
        this.initialTypes = (byte[])arrby.clone();
        this.paragraphEmbeddingLevel = by;
        this.runAlgorithm();
    }

    public BidiOrder(char[] arrc, int n, int n2, byte by) {
        this.initialTypes = new byte[n2];
        for (int i = 0; i < n2; ++i) {
            this.initialTypes[i] = rtypes[arrc[n + i]];
        }
        BidiOrder.validateParagraphEmbeddingLevel(by);
        this.paragraphEmbeddingLevel = by;
        this.runAlgorithm();
    }

    public static final byte getDirection(char c) {
        return rtypes[c];
    }

    private void runAlgorithm() {
        this.textLength = this.initialTypes.length;
        this.resultTypes = (byte[])this.initialTypes.clone();
        if (this.paragraphEmbeddingLevel == -1) {
            this.determineParagraphEmbeddingLevel();
        }
        this.resultLevels = new byte[this.textLength];
        this.setLevels(0, this.textLength, this.paragraphEmbeddingLevel);
        this.determineExplicitEmbeddingLevels();
        this.textLength = this.removeExplicitCodes();
        byte by = this.paragraphEmbeddingLevel;
        int n = 0;
        while (n < this.textLength) {
            int n2;
            byte by2 = this.resultLevels[n];
            byte by3 = BidiOrder.typeForLevel(Math.max(by, by2));
            for (n2 = n + 1; n2 < this.textLength && this.resultLevels[n2] == by2; ++n2) {
            }
            byte by4 = n2 < this.textLength ? this.resultLevels[n2] : this.paragraphEmbeddingLevel;
            byte by5 = BidiOrder.typeForLevel(Math.max(by4, by2));
            this.resolveWeakTypes(n, n2, by2, by3, by5);
            this.resolveNeutralTypes(n, n2, by2, by3, by5);
            this.resolveImplicitLevels(n, n2, by2, by3, by5);
            by = by2;
            n = n2;
        }
        this.textLength = this.reinsertExplicitCodes(this.textLength);
    }

    private void determineParagraphEmbeddingLevel() {
        int n = -1;
        for (int i = 0; i < this.textLength; ++i) {
            int n2 = this.resultTypes[i];
            if (n2 != 0 && n2 != 4 && n2 != 3) continue;
            n = n2;
            break;
        }
        this.paragraphEmbeddingLevel = n == -1 ? 0 : (n == 0 ? 0 : 1);
    }

    private void determineExplicitEmbeddingLevels() {
        this.embeddings = BidiOrder.processEmbeddings(this.resultTypes, this.paragraphEmbeddingLevel);
        for (int i = 0; i < this.textLength; ++i) {
            byte by = this.embeddings[i];
            if ((by & 128) != 0) {
                by = (byte)(by & 127);
                this.resultTypes[i] = BidiOrder.typeForLevel(by);
            }
            this.resultLevels[i] = by;
        }
    }

    private int removeExplicitCodes() {
        int n = 0;
        for (int i = 0; i < this.textLength; ++i) {
            byte by = this.initialTypes[i];
            if (by == 1 || by == 5 || by == 2 || by == 6 || by == 7 || by == 14) continue;
            this.embeddings[n] = this.embeddings[i];
            this.resultTypes[n] = this.resultTypes[i];
            this.resultLevels[n] = this.resultLevels[i];
            ++n;
        }
        return n;
    }

    private int reinsertExplicitCodes(int n) {
        int n2 = this.initialTypes.length;
        while (--n2 >= 0) {
            byte by = this.initialTypes[n2];
            if (by == 1 || by == 5 || by == 2 || by == 6 || by == 7 || by == 14) {
                this.embeddings[n2] = 0;
                this.resultTypes[n2] = by;
                this.resultLevels[n2] = -1;
                continue;
            }
            this.embeddings[n2] = this.embeddings[--n];
            this.resultTypes[n2] = this.resultTypes[n];
            this.resultLevels[n2] = this.resultLevels[n];
        }
        if (this.resultLevels[0] == -1) {
            this.resultLevels[0] = this.paragraphEmbeddingLevel;
        }
        for (n2 = 1; n2 < this.initialTypes.length; ++n2) {
            if (this.resultLevels[n2] != -1) continue;
            this.resultLevels[n2] = this.resultLevels[n2 - 1];
        }
        return this.initialTypes.length;
    }

    private static byte[] processEmbeddings(byte[] arrby, byte by) {
        int n = arrby.length;
        byte[] arrby2 = new byte[n];
        byte[] arrby3 = new byte[62];
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        byte by2 = by;
        byte by3 = by;
        block5 : for (int i = 0; i < n; ++i) {
            arrby2[i] = by3;
            byte by4 = arrby[i];
            switch (by4) {
                case 1: 
                case 2: 
                case 5: 
                case 6: {
                    if (n4 == 0) {
                        byte by5 = by4 == 5 || by4 == 6 ? (byte)(by2 + 1 | 1) : (byte)(by2 + 2 & -2);
                        if (by5 < 62) {
                            arrby3[n2] = by3;
                            ++n2;
                            by2 = by5;
                            by3 = by4 == 2 || by4 == 6 ? (byte)(by5 | 128) : by5;
                            arrby2[i] = by3;
                            continue block5;
                        }
                        if (by2 == 60) {
                            ++n3;
                            continue block5;
                        }
                    }
                    ++n4;
                    continue block5;
                }
                case 7: {
                    if (n4 > 0) {
                        --n4;
                        continue block5;
                    }
                    if (n3 > 0 && by2 != 61) {
                        --n3;
                        continue block5;
                    }
                    if (n2 <= 0) continue block5;
                    by3 = arrby3[--n2];
                    by2 = (byte)(by3 & 127);
                    continue block5;
                }
                case 15: {
                    n2 = 0;
                    n4 = 0;
                    n3 = 0;
                    by2 = by;
                    by3 = by;
                    arrby2[i] = by;
                    break;
                }
            }
        }
        return arrby2;
    }

    private void resolveWeakTypes(int n, int n2, byte by, byte n3, byte by2) {
        int n4;
        int n5;
        int n6;
        int n7;
        int n8 = n3;
        for (n4 = n; n4 < n2; ++n4) {
            n7 = this.resultTypes[n4];
            if (n7 == 13) {
                this.resultTypes[n4] = n8;
                continue;
            }
            n8 = n7;
        }
        block1 : for (n4 = n; n4 < n2; ++n4) {
            if (this.resultTypes[n4] != 8) continue;
            for (n7 = n4 - 1; n7 >= n; --n7) {
                n6 = this.resultTypes[n7];
                if (n6 != 0 && n6 != 3 && n6 != 4) continue;
                if (n6 != 4) continue block1;
                this.resultTypes[n4] = 11;
                continue block1;
            }
        }
        for (n4 = n; n4 < n2; ++n4) {
            if (this.resultTypes[n4] != 4) continue;
            this.resultTypes[n4] = 3;
        }
        for (n4 = n + 1; n4 < n2 - 1; ++n4) {
            if (this.resultTypes[n4] != 9 && this.resultTypes[n4] != 12) continue;
            n7 = this.resultTypes[n4 - 1];
            n6 = this.resultTypes[n4 + 1];
            if (n7 == 8 && n6 == 8) {
                this.resultTypes[n4] = 8;
                continue;
            }
            if (this.resultTypes[n4] != 12 || n7 != 11 || n6 != 11) continue;
            this.resultTypes[n4] = 11;
        }
        for (n4 = n; n4 < n2; ++n4) {
            if (this.resultTypes[n4] != 10) continue;
            n7 = n4;
            n6 = this.findRunLimit(n7, n2, new byte[]{10});
            int n9 = n5 = n7 == n ? n3 : this.resultTypes[n7 - 1];
            if (n5 != 8) {
                n5 = n6 == n2 ? by2 : this.resultTypes[n6];
            }
            if (n5 == 8) {
                this.setTypes(n7, n6, 8);
            }
            n4 = n6;
        }
        for (n4 = n; n4 < n2; ++n4) {
            n7 = this.resultTypes[n4];
            if (n7 != 9 && n7 != 10 && n7 != 12) continue;
            this.resultTypes[n4] = 18;
        }
        for (n4 = n; n4 < n2; ++n4) {
            if (this.resultTypes[n4] != 8) continue;
            n7 = n3;
            for (n6 = n4 - 1; n6 >= n; --n6) {
                n5 = this.resultTypes[n6];
                if (n5 != 0 && n5 != 3) continue;
                n7 = n5;
                break;
            }
            if (n7 != 0) continue;
            this.resultTypes[n4] = 0;
        }
    }

    private void resolveNeutralTypes(int n, int n2, byte by, byte by2, byte by3) {
        for (int i = n; i < n2; ++i) {
            byte by4;
            byte by5;
            byte by6 = this.resultTypes[i];
            if (by6 != 17 && by6 != 18 && by6 != 15 && by6 != 16) continue;
            int n3 = i;
            int n4 = this.findRunLimit(n3, n2, new byte[]{15, 16, 17, 18});
            if (n3 == n) {
                by5 = by2;
            } else {
                by5 = this.resultTypes[n3 - 1];
                if (by5 != 0 && by5 != 3) {
                    if (by5 == 11) {
                        by5 = 3;
                    } else if (by5 == 8) {
                        by5 = 3;
                    }
                }
            }
            if (n4 == n2) {
                by4 = by3;
            } else {
                by4 = this.resultTypes[n4];
                if (by4 != 0 && by4 != 3) {
                    if (by4 == 11) {
                        by4 = 3;
                    } else if (by4 == 8) {
                        by4 = 3;
                    }
                }
            }
            byte by7 = by5 == by4 ? by5 : BidiOrder.typeForLevel(by);
            this.setTypes(n3, n4, by7);
            i = n4;
        }
    }

    private void resolveImplicitLevels(int n, int n2, byte by, byte by2, byte by3) {
        if ((by & 1) == 0) {
            for (int i = n; i < n2; ++i) {
                byte by4 = this.resultTypes[i];
                if (by4 == 0) continue;
                if (by4 == 3) {
                    byte[] arrby = this.resultLevels;
                    int n3 = i;
                    arrby[n3] = (byte)(arrby[n3] + 1);
                    continue;
                }
                byte[] arrby = this.resultLevels;
                int n4 = i;
                arrby[n4] = (byte)(arrby[n4] + 2);
            }
        } else {
            for (int i = n; i < n2; ++i) {
                byte by5 = this.resultTypes[i];
                if (by5 == 3) continue;
                byte[] arrby = this.resultLevels;
                int n5 = i;
                arrby[n5] = (byte)(arrby[n5] + 1);
            }
        }
    }

    public byte[] getLevels() {
        return this.getLevels(new int[]{this.textLength});
    }

    public byte[] getLevels(int[] arrn) {
        int n;
        int n2;
        int n3;
        BidiOrder.validateLineBreaks(arrn, this.textLength);
        byte[] arrby = (byte[])this.resultLevels.clone();
        for (n = 0; n < arrby.length; ++n) {
            n2 = this.initialTypes[n];
            if (n2 != 15 && n2 != 16) continue;
            arrby[n] = this.paragraphEmbeddingLevel;
            for (n3 = n - 1; n3 >= 0 && BidiOrder.isWhitespace(this.initialTypes[n3]); --n3) {
                arrby[n3] = this.paragraphEmbeddingLevel;
            }
        }
        n = 0;
        for (n2 = 0; n2 < arrn.length; ++n2) {
            n3 = arrn[n2];
            for (int i = n3 - 1; i >= n && BidiOrder.isWhitespace(this.initialTypes[i]); --i) {
                arrby[i] = this.paragraphEmbeddingLevel;
            }
            n = n3;
        }
        return arrby;
    }

    public int[] getReordering(int[] arrn) {
        BidiOrder.validateLineBreaks(arrn, this.textLength);
        byte[] arrby = this.getLevels(arrn);
        return BidiOrder.computeMultilineReordering(arrby, arrn);
    }

    private static int[] computeMultilineReordering(byte[] arrby, int[] arrn) {
        int[] arrn2 = new int[arrby.length];
        int n = 0;
        for (int i = 0; i < arrn.length; ++i) {
            int n2 = arrn[i];
            byte[] arrby2 = new byte[n2 - n];
            System.arraycopy(arrby, n, arrby2, 0, arrby2.length);
            int[] arrn3 = BidiOrder.computeReordering(arrby2);
            for (int j = 0; j < arrn3.length; ++j) {
                arrn2[n + j] = arrn3[j] + n;
            }
            n = n2;
        }
        return arrn2;
    }

    private static int[] computeReordering(byte[] arrby) {
        int n;
        int n2;
        int n3 = arrby.length;
        int[] arrn = new int[n3];
        int n4 = 0;
        while (n4 < n3) {
            arrn[n4] = n4++;
        }
        n4 = 0;
        int n5 = 63;
        for (n2 = 0; n2 < n3; ++n2) {
            n = arrby[n2];
            if (n > n4) {
                n4 = n;
            }
            if ((n & 1) == 0 || n >= n5) continue;
            n5 = n;
        }
        for (n2 = n4; n2 >= n5; --n2) {
            for (n = 0; n < n3; ++n) {
                int n6;
                if (arrby[n] < n2) continue;
                int n7 = n;
                for (n6 = n + 1; n6 < n3 && arrby[n6] >= n2; ++n6) {
                }
                int n8 = n7;
                for (int i = n6 - 1; n8 < i; ++n8, --i) {
                    int n9 = arrn[n8];
                    arrn[n8] = arrn[i];
                    arrn[i] = n9;
                }
                n = n6;
            }
        }
        return arrn;
    }

    public byte getBaseLevel() {
        return this.paragraphEmbeddingLevel;
    }

    private static boolean isWhitespace(byte by) {
        switch (by) {
            case 1: 
            case 2: 
            case 5: 
            case 6: 
            case 7: 
            case 14: 
            case 17: {
                return true;
            }
        }
        return false;
    }

    private static byte typeForLevel(int n) {
        return (n & 1) == 0 ? 0 : 3;
    }

    private int findRunLimit(int n, int n2, byte[] arrby) {
        --n;
        block0 : while (++n < n2) {
            byte by = this.resultTypes[n];
            for (int i = 0; i < arrby.length; ++i) {
                if (by == arrby[i]) continue block0;
            }
            return n;
        }
        return n2;
    }

    private int findRunStart(int n, byte[] arrby) {
        block0 : while (--n >= 0) {
            byte by = this.resultTypes[n];
            for (int i = 0; i < arrby.length; ++i) {
                if (by == arrby[i]) continue block0;
            }
            return n + 1;
        }
        return 0;
    }

    private void setTypes(int n, int n2, byte by) {
        for (int i = n; i < n2; ++i) {
            this.resultTypes[i] = by;
        }
    }

    private void setLevels(int n, int n2, byte by) {
        for (int i = n; i < n2; ++i) {
            this.resultLevels[i] = by;
        }
    }

    private static void validateTypes(byte[] arrby) {
        int n;
        if (arrby == null) {
            throw new IllegalArgumentException("types is null");
        }
        for (n = 0; n < arrby.length; ++n) {
            if (arrby[n] >= 0 && arrby[n] <= 18) continue;
            throw new IllegalArgumentException("illegal type value at " + n + ": " + arrby[n]);
        }
        for (n = 0; n < arrby.length - 1; ++n) {
            if (arrby[n] != 15) continue;
            throw new IllegalArgumentException("B type before end of paragraph at index: " + n);
        }
    }

    private static void validateParagraphEmbeddingLevel(byte by) {
        if (by != -1 && by != 0 && by != 1) {
            throw new IllegalArgumentException("illegal paragraph embedding level: " + by);
        }
    }

    private static void validateLineBreaks(int[] arrn, int n) {
        int n2 = 0;
        for (int i = 0; i < arrn.length; ++i) {
            int n3 = arrn[i];
            if (n3 <= n2) {
                throw new IllegalArgumentException("bad linebreak: " + n3 + " at index: " + i);
            }
            n2 = n3;
        }
        if (n2 != n) {
            throw new IllegalArgumentException("last linebreak must be at " + n);
        }
    }

    static {
        for (int i = 0; i < baseTypes.length; ++i) {
            int n = baseTypes[i];
            char c = baseTypes[++i];
            byte by = (byte)baseTypes[++i];
            while (n <= c) {
                BidiOrder.rtypes[n++] = by;
            }
        }
    }
}

