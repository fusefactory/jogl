/*
 * $Id: Fixed.java,v 1.1.1.1 2004-12-05 23:14:26 davidsch Exp $
 *
 * Typecast - The Font Development Environment
 *
 * Copyright (c) 2004 David Schweinsberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.java.dev.typecast.ot;

/**
 * Functions for working with signed 16.16 fixed values
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: Fixed.java,v 1.1.1.1 2004-12-05 23:14:26 davidsch Exp $
 */
public class Fixed {

    // Tangent LUT
    static private int[] t = {
        0x0,
        0x1,
        0x3,
        0x4,
        0x6,
        0x7,
        0x9,
        0xb,
        0xc,
        0xe,
        0xf,
        0x11,
        0x12,
        0x14,
        0x16,
        0x17,
        0x19,
        0x1a,
        0x1c,
        0x1d,
        0x1f,
        0x21,
        0x22,
        0x24,
        0x25,
        0x27,
        0x29,
        0x2a,
        0x2c,
        0x2e,
        0x2f,
        0x31,
        0x32,
        0x34,
        0x36,
        0x37,
        0x39,
        0x3b,
        0x3c,
        0x3e,
        0x40,
        0x41,
        0x43,
        0x45,
        0x46,
        0x48,
        0x4a,
        0x4b,
        0x4d,
        0x4f,
        0x51,
        0x52,
        0x54,
        0x56,
        0x58,
        0x59,
        0x5b,
        0x5d,
        0x5f,
        0x60,
        0x62,
        0x64,
        0x66,
        0x68,
        0x6a,
        0x6b,
        0x6d,
        0x6f,
        0x71,
        0x73,
        0x75,
        0x77,
        0x79,
        0x7b,
        0x7c,
        0x7e,
        0x80,
        0x82,
        0x84,
        0x86,
        0x88,
        0x8a,
        0x8c,
        0x8e,
        0x91,
        0x93,
        0x95,
        0x97,
        0x99,
        0x9b,
        0x9d,
        0x9f,
        0xa2,
        0xa4,
        0xa6,
        0xa8,
        0xab,
        0xad,
        0xaf,
        0xb1,
        0xb4,
        0xb6,
        0xb9,
        0xbb,
        0xbd,
        0xc0,
        0xc2,
        0xc5,
        0xc7,
        0xca,
        0xcc,
        0xcf,
        0xd2,
        0xd4,
        0xd7,
        0xda,
        0xdc,
        0xdf,
        0xe2,
        0xe5,
        0xe8,
        0xea,
        0xed,
        0xf0,
        0xf3,
        0xf6,
        0xf9,
        0xfc,
        0x100,
        0x103,
        0x106,
        0x109,
        0x10c,
        0x110,
        0x113,
        0x116,
        0x11a,
        0x11d,
        0x121,
        0x125,
        0x128,
        0x12c,
        0x130,
        0x134,
        0x137,
        0x13b,
        0x13f,
        0x143,
        0x148,
        0x14c,
        0x150,
        0x154,
        0x159,
        0x15d,
        0x162,
        0x166,
        0x16b,
        0x170,
        0x175,
        0x17a,
        0x17f,
        0x184,
        0x189,
        0x18e,
        0x194,
        0x199,
        0x19f,
        0x1a5,
        0x1ab,
        0x1b1,
        0x1b7,
        0x1bd,
        0x1c3,
        0x1ca,
        0x1d1,
        0x1d7,
        0x1de,
        0x1e6,
        0x1ed,
        0x1f4,
        0x1fc,
        0x204,
        0x20c,
        0x214,
        0x21d,
        0x225,
        0x22e,
        0x238,
        0x241,
        0x24b,
        0x255,
        0x25f,
        0x26a,
        0x274,
        0x280,
        0x28b,
        0x297,
        0x2a3,
        0x2b0,
        0x2bd,
        0x2cb,
        0x2d9,
        0x2e8,
        0x2f7,
        0x306,
        0x317,
        0x328,
        0x339,
        0x34b,
        0x35e,
        0x372,
        0x387,
        0x39d,
        0x3b3,
        0x3cb,
        0x3e4,
        0x3fe,
        0x419,
        0x435,
        0x454,
        0x474,
        0x495,
        0x4b9,
        0x4de,
        0x506,
        0x531,
        0x55e,
        0x58f,
        0x5c3,
        0x5fb,
        0x637,
        0x677,
        0x6bd,
        0x709,
        0x75c,
        0x7b7,
        0x81b,
        0x889,
        0x904,
        0x98d,
        0xa27,
        0xad5,
        0xb9c,
        0xc82,
        0xd8e,
        0xecb,
        0x1046,
        0x1217,
        0x145a,
        0x1744,
        0x1b26,
        0x2095,
        0x28bc,
        0x3651,
        0x517b,
        0xa2f8
    };

    // Sine LUT
    static private int[] s = {
        0x0,
        0x1,
        0x3,
        0x4,
        0x6,
        0x7,
        0x9,
        0xa,
        0xc,
        0xe,
        0xf,
        0x11,
        0x12,
        0x14,
        0x15,
        0x17,
        0x19,
        0x1a,
        0x1c,
        0x1d,
        0x1f,
        0x20,
        0x22,
        0x24,
        0x25,
        0x27,
        0x28,
        0x2a,
        0x2b,
        0x2d,
        0x2e,
        0x30,
        0x31,
        0x33,
        0x35,
        0x36,
        0x38,
        0x39,
        0x3b,
        0x3c,
        0x3e,
        0x3f,
        0x41,
        0x42,
        0x44,
        0x45,
        0x47,
        0x48,
        0x4a,
        0x4b,
        0x4d,
        0x4e,
        0x50,
        0x51,
        0x53,
        0x54,
        0x56,
        0x57,
        0x59,
        0x5a,
        0x5c,
        0x5d,
        0x5f,
        0x60,
        0x61,
        0x63,
        0x64,
        0x66,
        0x67,
        0x69,
        0x6a,
        0x6c,
        0x6d,
        0x6e,
        0x70,
        0x71,
        0x73,
        0x74,
        0x75,
        0x77,
        0x78,
        0x7a,
        0x7b,
        0x7c,
        0x7e,
        0x7f,
        0x80,
        0x82,
        0x83,
        0x84,
        0x86,
        0x87,
        0x88,
        0x8a,
        0x8b,
        0x8c,
        0x8e,
        0x8f,
        0x90,
        0x92,
        0x93,
        0x94,
        0x95,
        0x97,
        0x98,
        0x99,
        0x9b,
        0x9c,
        0x9d,
        0x9e,
        0x9f,
        0xa1,
        0xa2,
        0xa3,
        0xa4,
        0xa6,
        0xa7,
        0xa8,
        0xa9,
        0xaa,
        0xab,
        0xad,
        0xae,
        0xaf,
        0xb0,
        0xb1,
        0xb2,
        0xb3,
        0xb5,
        0xb6,
        0xb7,
        0xb8,
        0xb9,
        0xba,
        0xbb,
        0xbc,
        0xbd,
        0xbe,
        0xbf,
        0xc0,
        0xc1,
        0xc2,
        0xc3,
        0xc4,
        0xc5,
        0xc6,
        0xc7,
        0xc8,
        0xc9,
        0xca,
        0xcb,
        0xcc,
        0xcd,
        0xce,
        0xcf,
        0xd0,
        0xd1,
        0xd2,
        0xd3,
        0xd3,
        0xd4,
        0xd5,
        0xd6,
        0xd7,
        0xd8,
        0xd9,
        0xd9,
        0xda,
        0xdb,
        0xdc,
        0xdd,
        0xdd,
        0xde,
        0xdf,
        0xe0,
        0xe1,
        0xe1,
        0xe2,
        0xe3,
        0xe3,
        0xe4,
        0xe5,
        0xe6,
        0xe6,
        0xe7,
        0xe8,
        0xe8,
        0xe9,
        0xea,
        0xea,
        0xeb,
        0xeb,
        0xec,
        0xed,
        0xed,
        0xee,
        0xee,
        0xef,
        0xef,
        0xf0,
        0xf1,
        0xf1,
        0xf2,
        0xf2,
        0xf3,
        0xf3,
        0xf4,
        0xf4,
        0xf4,
        0xf5,
        0xf5,
        0xf6,
        0xf6,
        0xf7,
        0xf7,
        0xf7,
        0xf8,
        0xf8,
        0xf9,
        0xf9,
        0xf9,
        0xfa,
        0xfa,
        0xfa,
        0xfb,
        0xfb,
        0xfb,
        0xfb,
        0xfc,
        0xfc,
        0xfc,
        0xfc,
        0xfd,
        0xfd,
        0xfd,
        0xfd,
        0xfe,
        0xfe,
        0xfe,
        0xfe,
        0xfe,
        0xfe,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff
    };

    // Cosine LUT
    static private int[] c = {
        0x100,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xff,
        0xfe,
        0xfe,
        0xfe,
        0xfe,
        0xfe,
        0xfe,
        0xfd,
        0xfd,
        0xfd,
        0xfd,
        0xfc,
        0xfc,
        0xfc,
        0xfc,
        0xfb,
        0xfb,
        0xfb,
        0xfb,
        0xfa,
        0xfa,
        0xfa,
        0xf9,
        0xf9,
        0xf9,
        0xf8,
        0xf8,
        0xf7,
        0xf7,
        0xf7,
        0xf6,
        0xf6,
        0xf5,
        0xf5,
        0xf4,
        0xf4,
        0xf4,
        0xf3,
        0xf3,
        0xf2,
        0xf2,
        0xf1,
        0xf1,
        0xf0,
        0xef,
        0xef,
        0xee,
        0xee,
        0xed,
        0xed,
        0xec,
        0xeb,
        0xeb,
        0xea,
        0xea,
        0xe9,
        0xe8,
        0xe8,
        0xe7,
        0xe6,
        0xe6,
        0xe5,
        0xe4,
        0xe3,
        0xe3,
        0xe2,
        0xe1,
        0xe1,
        0xe0,
        0xdf,
        0xde,
        0xdd,
        0xdd,
        0xdc,
        0xdb,
        0xda,
        0xd9,
        0xd9,
        0xd8,
        0xd7,
        0xd6,
        0xd5,
        0xd4,
        0xd3,
        0xd3,
        0xd2,
        0xd1,
        0xd0,
        0xcf,
        0xce,
        0xcd,
        0xcc,
        0xcb,
        0xca,
        0xc9,
        0xc8,
        0xc7,
        0xc6,
        0xc5,
        0xc4,
        0xc3,
        0xc2,
        0xc1,
        0xc0,
        0xbf,
        0xbe,
        0xbd,
        0xbc,
        0xbb,
        0xba,
        0xb9,
        0xb8,
        0xb7,
        0xb6,
        0xb5,
        0xb3,
        0xb2,
        0xb1,
        0xb0,
        0xaf,
        0xae,
        0xad,
        0xab,
        0xaa,
        0xa9,
        0xa8,
        0xa7,
        0xa6,
        0xa4,
        0xa3,
        0xa2,
        0xa1,
        0x9f,
        0x9e,
        0x9d,
        0x9c,
        0x9b,
        0x99,
        0x98,
        0x97,
        0x95,
        0x94,
        0x93,
        0x92,
        0x90,
        0x8f,
        0x8e,
        0x8c,
        0x8b,
        0x8a,
        0x88,
        0x87,
        0x86,
        0x84,
        0x83,
        0x82,
        0x80,
        0x7f,
        0x7e,
        0x7c,
        0x7b,
        0x7a,
        0x78,
        0x77,
        0x75,
        0x74,
        0x73,
        0x71,
        0x70,
        0x6e,
        0x6d,
        0x6c,
        0x6a,
        0x69,
        0x67,
        0x66,
        0x64,
        0x63,
        0x61,
        0x60,
        0x5f,
        0x5d,
        0x5c,
        0x5a,
        0x59,
        0x57,
        0x56,
        0x54,
        0x53,
        0x51,
        0x50,
        0x4e,
        0x4d,
        0x4b,
        0x4a,
        0x48,
        0x47,
        0x45,
        0x44,
        0x42,
        0x41,
        0x3f,
        0x3e,
        0x3c,
        0x3b,
        0x39,
        0x38,
        0x36,
        0x35,
        0x33,
        0x31,
        0x30,
        0x2e,
        0x2d,
        0x2b,
        0x2a,
        0x28,
        0x27,
        0x25,
        0x24,
        0x22,
        0x20,
        0x1f,
        0x1d,
        0x1c,
        0x1a,
        0x19,
        0x17,
        0x15,
        0x14,
        0x12,
        0x11,
        0xf,
        0xe,
        0xc,
        0xa,
        0x9,
        0x7,
        0x6,
        0x4,
        0x3,
        0x1
    };

    /**
     * Yet to be implemented.
     * @param num Input
     * @return Output
     */
    public static int arctan( int num ) {
        return 0;
    }

    /**
     * 26.6 fixed number square root function.
     * Simple (brain-dead) divide & conqure algorithm.
     * @param num The 26.6 fixed number in question
     * @return The resulting square root
     */
    public static int squareRoot(int num) {
        int n = num;
        int divisor = num;
        int nSquared;

        while (divisor != 0) {
            divisor /= 2;
            nSquared = (n * n) >> 6;
            if (nSquared == num) {
                break;
            } else if (nSquared > num) {
                n -= divisor;
            } else {
                n += divisor;
            }
        }
        return n;
    }
    
    public static float floatValue(long fixed) {
        return (fixed >> 16) + (float)(fixed & 0xffff) / 0x10000;
    }
    
    public static float roundedFloatValue(long fixed, int decimalPlaces) {
        int factor = 10 * decimalPlaces;
        return (float)((int)(floatValue(fixed) * factor)) / factor;
    }
}
