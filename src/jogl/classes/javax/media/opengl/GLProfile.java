/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

package javax.media.opengl;

import javax.media.opengl.fixedfunc.*;
import java.lang.reflect.*;
import java.util.HashMap;
import java.security.*;
import com.sun.opengl.impl.*;
import com.sun.nativewindow.impl.NWReflection;

/**
 * Manages all available OpenGL Profiles.
 * Each GLProfile is a singleton instance queried at static initialization time.
 * All returned GLProfile objects are references to such singletons.
 */
public class GLProfile implements Cloneable {
  public static final boolean DEBUG = Debug.debug("GLProfile");

  //
  // Public (user-visible) profiles
  //

  /** The desktop OpenGL >= 3.1 profile */
  public static final String GL3   = "GL3";

  /** The desktop OpenGL [1.5 .. 3.0] profile */
  public static final String GL2   = "GL2";

  /** The embedded OpenGL ES >= 1.0 profile */
  public static final String GLES1 = "GLES1";

  /** The embedded OpenGL ES >= 2.0 profile */
  public static final String GLES2 = "GLES2";

  /** The intersection of the desktop GL2 and embedded ES1 profiles */
  public static final String GL2ES1 = "GL2ES1";

  /** The intersection of the desktop GL2 and embedded ES2 profiles */
  public static final String GL2ES2 = "GL2ES2";

  /** 
   * All GL Profiles in the order of default detection.
   * Order: GL2, GL2ES2, GL2ES1, GL3, GLES2, GLES1
   */
  public static final String[] GL_PROFILE_LIST = new String[] { GL2, GL2ES2, GL2ES1, GL3, GLES2, GLES1 };

  /**
   * All GL2ES2 Profiles in the order of default detection 
   * Order: GL2ES2, GL2, GL3, GLES2
   */
  public static final String[] GL2ES2_PROFILE_LIST = new String[] { GL2ES2, GL2, GL3, GLES2 };

  /**
   * All GL2ES1 Profiles in the order of default detection 
   * Order: GL2ES1, GL2, GLES1
   */
  public static final String[] GL2ES1_PROFILE_LIST = new String[] { GL2ES1, GL2, GLES1 };

  /** Returns a default GLProfile object, reflecting the best for the running platform.
    * It selects the first of the set {@link GLProfile#GL_PROFILE_LIST}
    */
  public static final GLProfile GetProfileDefault() {
    if(null==defaultGLProfile) {
        throw new GLException("No default profile available"); // should never be reached 
    }
    return defaultGLProfile;
  }

  /** Returns a GLProfile object.
    * Verfifies the given profile and chooses an apropriate implementation.
    *
    * @throws GLException if no implementation for the given profile is found.
    */
  public static final GLProfile GetProfile(String profile) 
    throws GLException
  {
    if(null==profile) return GetProfileDefault();
    GLProfile glProfile = (GLProfile) mappedProfiles.get(profile);
    if(null==glProfile) {
        throw new GLException("No implementation for profile "+profile+" available");
    }
    return glProfile;
  }

  /**
   * Returns a profile, implementing the interface GL2ES1.
   * It selects the first of the set: {@link GLProfile#GL2ES1_PROFILE_LIST}
   *
   * @throws GLException if no implementation for the given profile is found.
   */
  public static final GLProfile GetProfileGL2ES1() 
    throws GLException
  {
    return GetProfile(GL2ES1_PROFILE_LIST);
  }

  /**
   * Returns a profile, implementing the interface GL2ES2.
   * It selects the first of the set: {@link GLProfile#GL2ES2_PROFILE_LIST}
   *
   * @throws GLException if no implementation for the given profile is found.
   */
  public static final GLProfile GetProfileGL2ES2() 
    throws GLException
  {
    return GetProfile(GL2ES2_PROFILE_LIST);
  }

  /**
   * Returns the first profile from the given list,
   * where an implementation is available.
   *
   * @throws GLException if no implementation for the given profile is found.
   */
  public static final GLProfile GetProfile(String[] profiles) 
    throws GLException
  {
    for(int i=0; i<profiles.length; i++) {
        String profile = profiles[i];
        GLProfile glProfile = (GLProfile) mappedProfiles.get(profile);
        if(null!=glProfile) {
            return glProfile;
        }
    }
    throw new GLException("Profiles "+list2String(profiles)+" not available");
  }

  /** Indicates whether the native OpenGL ES1 profile is in use. 
   * This requires an EGL interface.
   */
  public static final boolean UsesNativeGLES1(String profileImpl) {
    return GLES1.equals(profileImpl) || GL2ES1.equals(profileImpl) ;
  }

  /** Indicates whether the native OpenGL ES2 profile is in use. 
   * This requires an EGL interface.
   */
  public static final boolean UsesNativeGLES2(String profileImpl) {
    return GLES2.equals(profileImpl) || GL2ES2.equals(profileImpl) ;
  }

  /** Indicates whether either of the native OpenGL ES profiles are in use. */
  public static final boolean UsesNativeGLES(String profileImpl) {
    return UsesNativeGLES2(profileImpl) || UsesNativeGLES1(profileImpl);
  }

  public static final String GetGLImplBaseClassName(String profileImpl) {
        if(GL3.equals(profileImpl)) {
            return "com.sun.opengl.impl.gl3.GL3";
        } else if(GL2.equals(profileImpl)) {
            return "com.sun.opengl.impl.gl2.GL2";
        } else if(GL2ES12.equals(profileImpl)) {
            return "com.sun.opengl.impl.gl2es12.GL2ES12";
        } else if(GLES1.equals(profileImpl) || GL2ES1.equals(profileImpl)) {
            return "com.sun.opengl.impl.es1.GLES1";
        } else if(GLES2.equals(profileImpl) || GL2ES2.equals(profileImpl)) {
            return "com.sun.opengl.impl.es2.GLES2";
        } else {
            throw new GLException("unsupported profile \"" + profileImpl + "\"");
        }
  }

  public final String getGLImplBaseClassName() {
    return GetGLImplBaseClassName(profileImpl);
  }

  public Object clone() {
    try {
      return super.clone();
    } catch (Exception e) {
      throw new GLException(e);
    }
  }

  /**
   * @param o GLProfile object to compare with
   * @return true if given Object is a GLProfile and
   *         if both, profile and profileImpl is equal with this.
   */
  public final boolean equals(Object o) {
    if(o instanceof GLProfile) {
        GLProfile glp = (GLProfile)o;
        return profile.equals(glp.getName()) &&
               profileImpl.equals(glp.getImplName()) ;
    }
    return false;
  }
 
  /**
   * @param glp GLProfile to compare with
   * @throws GLException if given GLProfile and this aren't equal
   */
  public final void verifyEquality(GLProfile glp) 
    throws GLException
  {
    if(!this.equals(glp)) throw new GLException("GLProfiles are not equal: "+this+" != "+glp);
  }

  public final String getName() {
    return profile;
  }

  public final String getImplName() {
    return profileImpl;
  }

  /** Indicates whether this profile is capable os GL3. */
  public final boolean isGL3() {
    return GL3.equals(profile);
  }

  /** Indicates whether this profile is capable os GL2. */
  public final boolean isGL2() {
    return GL2.equals(profile);
  }

  /** Indicates whether this profile is capable os GLES1. */
  public final boolean isGLES1() {
    return GLES1.equals(profile);
  }

  /** Indicates whether this profile is capable os GLES2. */
  public final boolean isGLES2() {
    return GLES2.equals(profile);
  }

  /** Indicates whether this profile is capable os GL2ES1. */
  public final boolean isGL2ES1() {
    return GL2ES1.equals(profile) || isGL2() || isGLES1() ;
  }

  /** Indicates whether this profile is capable os GL2ES2. */
  public final boolean isGL2ES2() {
    return GL2ES2.equals(profile) || isGL2() || isGL3() || isGLES2() ;
  }

  /** Indicates whether this profile uses the native OpenGL ES1 implementations. */
  public final boolean usesNativeGLES1() {
    return GLES1.equals(profileImpl) || GL2ES1.equals(profileImpl) ;
  }

  /** Indicates whether this profile uses the native OpenGL ES2 implementations. */
  public final boolean usesNativeGLES2() {
    return GLES2.equals(profileImpl) || GL2ES2.equals(profileImpl) ;
  }

  /** Indicates whether this profile uses either of the native OpenGL ES implementations. */
  public final boolean usesNativeGLES() {
    return usesNativeGLES2() || usesNativeGLES1();
  }

  /** Indicates whether this profile supports GLSL. */
  public final boolean hasGLSL() {
    return isGL2ES2() ;
  }

  /** 
   * General validation if type is a valid GL data type
   * for the current profile
   */
  public boolean isValidDataType(int type, boolean throwException) {
    switch(type) {
        case GL.GL_UNSIGNED_BYTE:
        case GL.GL_BYTE:
        case GL.GL_UNSIGNED_SHORT:
        case GL.GL_SHORT:
        case GL.GL_FLOAT:
        case GL.GL_FIXED:
            return true;
        case javax.media.opengl.GL2ES2.GL_INT:
        case javax.media.opengl.GL2ES2.GL_UNSIGNED_INT:
            if( isGL2ES2() ) {
                return true;
            }
        case javax.media.opengl.GL2.GL_DOUBLE:
            if( isGL3() ) {
                return true;
            }
        case javax.media.opengl.GL2.GL_2_BYTES:
        case javax.media.opengl.GL2.GL_3_BYTES:
        case javax.media.opengl.GL2.GL_4_BYTES:
            if( isGL2() ) {
                return true;
            }
    } 
    if(throwException) {
        throw new GLException("Illegal data type on profile "+this+": "+type);
    }
    return false;
  }

  public boolean isValidArrayDataType(int index, int comps, int type, 
                                      boolean isVertexAttribPointer, boolean throwException) {
    String arrayName = GetGLArrayName(index);
    if(isGLES1()) {
        if(isVertexAttribPointer) {
            if(throwException) {
                throw new GLException("Illegal array type for "+arrayName+" on profile GLES1: VertexAttribPointer");
            }
            return false;
        }
        switch(index) {
            case GLPointerFunc.GL_VERTEX_ARRAY:
            case GLPointerFunc.GL_TEXTURE_COORD_ARRAY:
                switch(type) {
                    case GL.GL_BYTE:
                    case GL.GL_SHORT:
                    case GL.GL_FIXED:
                    case GL.GL_FLOAT:
                        break;
                    default: 
                        if(throwException) {
                            throw new GLException("Illegal data type for "+arrayName+" on profile GLES1: "+type);
                        }
                        return false;
                }
                switch(comps) {
                    case 0:
                    case 2:
                    case 3:
                    case 4:
                        break;
                    default: 
                        if(throwException) {
                            throw new GLException("Illegal component number for "+arrayName+" on profile GLES1: "+comps);
                        }
                        return false;
                }
                break;
            case GLPointerFunc.GL_NORMAL_ARRAY:
                switch(type) {
                    case GL.GL_BYTE:
                    case GL.GL_SHORT:
                    case GL.GL_FIXED:
                    case GL.GL_FLOAT:
                        break;
                    default: 
                        if(throwException) {
                            throw new GLException("Illegal data type for "+arrayName+" on profile GLES1: "+type);
                        }
                        return false;
                }
                switch(comps) {
                    case 0:
                    case 3:
                        break;
                    default: 
                        if(throwException) {
                            throw new GLException("Illegal component number for "+arrayName+" on profile GLES1: "+comps);
                        }
                        return false;
                }
                break;
            case GLPointerFunc.GL_COLOR_ARRAY:
                switch(type) {
                    case GL.GL_UNSIGNED_BYTE:
                    case GL.GL_FIXED:
                    case GL.GL_FLOAT:
                        break;
                    default: 
                        if(throwException) {
                            throw new GLException("Illegal data type for "+arrayName+" on profile GLES1: "+type);
                        }
                        return false;
                }
                switch(comps) {
                    case 0:
                    case 4:
                        break;
                    default: 
                        if(throwException) {
                            throw new GLException("Illegal component number for "+arrayName+" on profile GLES1: "+comps);
                        }
                        return false;
                }
                break;
        }
    } else if(isGLES2()) {
        // simply ignore !isVertexAttribPointer case, since it is simulated anyway ..

        switch(type) {
            case GL.GL_UNSIGNED_BYTE:
            case GL.GL_BYTE:
            case GL.GL_UNSIGNED_SHORT:
            case GL.GL_SHORT:
            case GL.GL_FLOAT:
            case GL.GL_FIXED:
                break;
            default: 
                if(throwException) {
                    throw new GLException("Illegal data type for "+arrayName+" on profile GLES2: "+type);
                }
                return false;
        }
        switch(comps) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                break;
            default: 
                if(throwException) {
                    throw new GLException("Illegal component number for "+arrayName+" on profile GLES1: "+comps);
                }
                return false;
        }
    } else if( isGL2ES2() ) {
        if(isVertexAttribPointer) {
            switch(type) {
                case GL.GL_UNSIGNED_BYTE:
                case GL.GL_BYTE:
                case GL.GL_UNSIGNED_SHORT:
                case GL.GL_SHORT:
                case GL.GL_FLOAT:
                case javax.media.opengl.GL2ES2.GL_INT:
                case javax.media.opengl.GL2ES2.GL_UNSIGNED_INT:
                case javax.media.opengl.GL2.GL_DOUBLE:
                    break;
                default: 
                    if(throwException) {
                        throw new GLException("Illegal data type for "+arrayName+" on profile GL2: "+type);
                    }
                    return false;
            }
            switch(comps) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    break;
                default: 
                    if(throwException) {
                        throw new GLException("Illegal component number for "+arrayName+" on profile GL2: "+comps);
                    }
                    return false;
            }
        } else {
            switch(index) {
                case GLPointerFunc.GL_VERTEX_ARRAY:
                    switch(type) {
                        case GL.GL_SHORT:
                        case GL.GL_FLOAT:
                        case javax.media.opengl.GL2ES2.GL_INT:
                        case javax.media.opengl.GL2.GL_DOUBLE:
                            break;
                        default: 
                            if(throwException) {
                                throw new GLException("Illegal data type for "+arrayName+" on profile GL2: "+type);
                            }
                            return false;
                    }
                    switch(comps) {
                        case 0:
                        case 2:
                        case 3:
                        case 4:
                            break;
                        default: 
                            if(throwException) {
                                throw new GLException("Illegal component number for "+arrayName+" on profile GL2: "+comps);
                            }
                            return false;
                    }
                    break;
                case GLPointerFunc.GL_NORMAL_ARRAY:
                    switch(type) {
                        case GL.GL_BYTE:
                        case GL.GL_SHORT:
                        case GL.GL_FLOAT:
                        case javax.media.opengl.GL2ES2.GL_INT:
                        case javax.media.opengl.GL2.GL_DOUBLE:
                            break;
                        default: 
                            if(throwException) {
                                throw new GLException("Illegal data type for "+arrayName+" on profile GL2: "+type);
                            }
                            return false;
                    }
                    switch(comps) {
                        case 0:
                        case 3:
                            break;
                        default: 
                            if(throwException) {
                                throw new GLException("Illegal component number for "+arrayName+" on profile GLES1: "+comps);
                            }
                            return false;
                    }
                    break;
                case GLPointerFunc.GL_COLOR_ARRAY:
                    switch(type) {
                        case GL.GL_UNSIGNED_BYTE:
                        case GL.GL_BYTE:
                        case GL.GL_UNSIGNED_SHORT:
                        case GL.GL_SHORT:
                        case GL.GL_FLOAT:
                        case javax.media.opengl.GL2ES2.GL_INT:
                        case javax.media.opengl.GL2ES2.GL_UNSIGNED_INT:
                        case javax.media.opengl.GL2.GL_DOUBLE:
                            break;
                        default: 
                            if(throwException) {
                                throw new GLException("Illegal data type for "+arrayName+" on profile GL2: "+type);
                            }
                            return false;
                    }
                    switch(comps) {
                        case 0:
                        case 3:
                        case 4:
                            break;
                        default: 
                            if(throwException) {
                                throw new GLException("Illegal component number for "+arrayName+" on profile GL2: "+comps);
                            }
                            return false;
                    }
                    break;
                case GLPointerFunc.GL_TEXTURE_COORD_ARRAY:
                    switch(type) {
                        case GL.GL_SHORT:
                        case GL.GL_FLOAT:
                        case javax.media.opengl.GL2ES2.GL_INT:
                        case javax.media.opengl.GL2.GL_DOUBLE:
                            break;
                        default: 
                            if(throwException) {
                                throw new GLException("Illegal data type for "+arrayName+" on profile GL2: "+type);
                            }
                            return false;
                    }
                    switch(comps) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            break;
                        default: 
                            if(throwException) {
                                throw new GLException("Illegal component number for "+arrayName+" on profile GL2: "+comps);
                            }
                            return false;
                    }
                    break;
            }
        }
    }
    return true;
  }

  public String toString() {
    return "GLProfile[" + profile + "/" + profileImpl + "]";
  }

  // The intersection between desktop OpenGL and the union of the OpenGL ES profiles
  // This is here only to avoid having separate GL2ES1Impl and GL2ES2Impl classes
  private static final String GL2ES12 = "GL2ES12";

  private static final boolean hasGL3Impl;
  private static final boolean hasGL2Impl;
  private static final boolean hasGL2ES12Impl;
  private static final boolean hasGLES2Impl;
  private static final boolean hasGLES1Impl;

  /** The JVM/process wide default GL profile **/
  private static GLProfile defaultGLProfile;
  
  /** All GLProfiles */
  private static final HashMap/*<String, GLProfile>*/ mappedProfiles;

  /**
   * Tries the profiles implementation and native libraries.
   * Throws an GLException if no profile could be found at all.
   */
  static {
    boolean hasDesktopGL = false;
    try {
        // See DRIHack.java for an explanation of why this is necessary
        DRIHack.begin();
        NativeLibLoader.loadGLDesktop();
        DRIHack.end();
        hasDesktopGL = true;
    } catch (Throwable t) {
        if (DEBUG) {
            System.err.println("GLProfile.static Desktop GL Library not available");
            t.printStackTrace();
        }
    }
    boolean hasDesktopGLES12 = false;
    try {
        // See DRIHack.java for an explanation of why this is necessary
        DRIHack.begin();
        NativeLibLoader.loadGLDesktopES12();
        DRIHack.end();
        hasDesktopGLES12 = true;
    } catch (Throwable t) {
        if (DEBUG) {
            System.err.println("GLProfile.static Desktop GL ES12 Library not available");
            t.printStackTrace();
        }
    }

    boolean hasNativeOSFactory = false;
    if(hasDesktopGL||hasDesktopGLES12) {
        try {
            hasNativeOSFactory = null!=GLDrawableFactory.getNativeOSFactory();
        } catch (Throwable t) {
            if (DEBUG) {
                System.err.println("GLProfile.static - Native platform GLDrawable factory not available");
                t.printStackTrace();
            }
        }
    }
    if(!hasNativeOSFactory) {
        hasDesktopGLES12=false;
        hasDesktopGL=false;
    }

    // FIXME: check for real GL3 availability .. ?
    hasGL3Impl     = hasDesktopGL && null!=NWReflection.getClass("com.sun.opengl.impl.gl3.GL3Impl");
    hasGL2Impl     = hasDesktopGL && null!=NWReflection.getClass("com.sun.opengl.impl.gl2.GL2Impl");

    hasGL2ES12Impl = hasDesktopGLES12 && null!=NWReflection.getClass("com.sun.opengl.impl.gl2es12.GL2ES12Impl");

    boolean btest = false;
    try {
        btest = null!=GLDrawableFactory.getFactory(GLES2) && null!=NWReflection.getClass("com.sun.opengl.impl.es2.GLES2Impl");
    } catch (Throwable t) {
        if (DEBUG) {
            System.err.println("GLProfile.static - GL ES2 Factory/Library not available");
            t.printStackTrace();
        }
    }
    hasGLES2Impl     = btest;

    btest = false;
    try {
        btest = null!=GLDrawableFactory.getFactory(GLES1) && null!=NWReflection.getClass("com.sun.opengl.impl.es1.GLES1Impl");
    } catch (Throwable t) {
        if (DEBUG) {
            System.err.println("GLProfile.static - GL ES1 Factory/Library not available");
            t.printStackTrace();
        }
    }
    hasGLES1Impl     = btest;

    if (DEBUG) {
        System.err.println("GLProfile.static hasNativeOSFactory "+hasNativeOSFactory);
        System.err.println("GLProfile.static hasDesktopGLES12 "+hasDesktopGLES12);
        System.err.println("GLProfile.static hasDesktopGL "+hasDesktopGL);
        System.err.println("GLProfile.static hasGL3Impl "+hasGL3Impl);
        System.err.println("GLProfile.static hasGL2Impl "+hasGL2Impl);
        System.err.println("GLProfile.static hasGL2ES12Impl "+hasGL2ES12Impl);
        System.err.println("GLProfile.static hasGLES2Impl "+hasGLES2Impl);
        System.err.println("GLProfile.static hasGLES1Impl "+hasGLES1Impl);
    }

    HashMap/*<String, GLProfile>*/ _mappedProfiles = new HashMap(GL_PROFILE_LIST.length);
    for(int i=0; i<GL_PROFILE_LIST.length; i++) {
        String profile = GL_PROFILE_LIST[i];
        String profileImpl = ComputeProfileImpl(profile);
        if(null!=profileImpl) {
            GLProfile glProfile = new GLProfile(profile, profileImpl);
            _mappedProfiles.put(profile, glProfile);
            if (DEBUG) {
                System.err.println("GLProfile.static map "+glProfile);
            }
            if(null==defaultGLProfile) {
                defaultGLProfile=glProfile;
                if (DEBUG) {
                    System.err.println("GLProfile.static default "+glProfile);
                }
            }
        } else {
            if (DEBUG) {
                System.err.println("GLProfile.static map *** no mapping for "+profile);
            }
        }
    }
    mappedProfiles = _mappedProfiles; // final ..
    if(null==defaultGLProfile) {
        throw new GLException("No profile available: "+list2String(GL_PROFILE_LIST));
    }
  }

  private static final String list2String(String[] list) {
    StringBuffer msg = new StringBuffer();
    msg.append("[");
    for (int i = 0; i < list.length; i++) {
      if (i > 0)
          msg.append(", ");
      msg.append(list[i]);
    }
    msg.append("]");
    return msg.toString();
  }


  /**
   * Returns the profile implementation
   */
  private static String ComputeProfileImpl(String profile) {
    if (GL2ES1.equals(profile)) {
      if(hasGL2Impl) {
          return GL2;
      } else if(hasGL2ES12Impl) {
          return GL2ES12;
      } else if(hasGLES1Impl) {
          return GLES1;
      }
    } else if (GL2ES2.equals(profile)) {
      if(hasGL2ES12Impl) {
          return GL2ES12;
      } else if(hasGL2Impl) {
          return GL2;
      } else if(hasGL3Impl) {
          return GL3;
      } else if(hasGLES2Impl) {
          return GLES2;
      }
    } else if(GL3.equals(profile) && hasGL3Impl) {
        return GL3;
    } else if(GL2.equals(profile) && hasGL2Impl) {
        return GL2;
    } else if(GLES2.equals(profile) && hasGLES2Impl) {
        return GLES2;
    } else if(GLES1.equals(profile) && hasGLES1Impl) {
        return GLES1;
    }
    return null;
  }

  public static String GetGLTypeName(int type) {
    switch (type) {
        case GL.GL_UNSIGNED_BYTE:
            return "GL_UNSIGNED_BYTE";
        case GL.GL_BYTE:
            return "GL_BYTE";
        case GL.GL_UNSIGNED_SHORT:
            return "GL_UNSIGNED_SHORT";
        case GL.GL_SHORT:
            return "GL_SHORT";
        case GL.GL_FLOAT:
            return "GL_FLOAT";
        case GL.GL_FIXED:
            return "GL_FIXED";
        case javax.media.opengl.GL2ES2.GL_INT:
            return "GL_INT";
        case javax.media.opengl.GL2ES2.GL_UNSIGNED_INT:
            return "GL_UNSIGNED_INT";
        case javax.media.opengl.GL2.GL_DOUBLE:
            return "GL_DOUBLE";
        case javax.media.opengl.GL2.GL_2_BYTES:
            return "GL_2_BYTES";
        case javax.media.opengl.GL2.GL_3_BYTES:
            return "GL_3_BYTES";
        case javax.media.opengl.GL2.GL_4_BYTES:
            return "GL_4_BYTES";
    }
    return null;
  }

  public static String GetGLArrayName(int array) {
      switch(array) {
          case GLPointerFunc.GL_VERTEX_ARRAY:
              return "GL_VERTEX_ARRAY";
          case GLPointerFunc.GL_NORMAL_ARRAY:
              return "GL_NORMAL_ARRAY";
          case GLPointerFunc.GL_COLOR_ARRAY:
              return "GL_COLOR_ARRAY";
          case GLPointerFunc.GL_TEXTURE_COORD_ARRAY:
              return "GL_TEXTURE_COORD_ARRAY";
      }
      return null;
  }

  private GLProfile(String profile, String profileImpl) {
    this.profile = profile;
    this.profileImpl = profileImpl;
  }

  private String profileImpl = null;
  private String profile = null;

}