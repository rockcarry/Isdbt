package kr.co.fci.tv;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by live.kim on 2017-02-23.
 */

public final class StorageHelper {

// private static final String TAG = "StorageHelper";

    /**
     * Instantiates a new storage helper.
     */
    public StorageHelper() {
    }

    /** The Constant STORAGES_ROOT. */
    private static final String STORAGES_ROOT;

    static {
        final String primaryStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        final int index = primaryStoragePath.indexOf(File.separatorChar, 1);
        if (index != -1) {
            STORAGES_ROOT = primaryStoragePath.substring(0, index + 1);
        } else {
            STORAGES_ROOT = File.separator;
        }
    }

    /** The Constant AVOIDED_DEVICES. */
    private static final String[] AVOIDED_DEVICES = new String[] { "rootfs", "tmpfs", "dvpts", "proc", "sysfs",
            "none" };

    /** The Constant AVOIDED_DIRECTORIES. */
    private static final String[] AVOIDED_DIRECTORIES = new String[] { "obb", "asec" };

    /** The Constant DISALLOWED_FILESYSTEMS. */
    private static final String[] DISALLOWED_FILESYSTEMS = new String[] { "tmpfs", "rootfs", "romfs", "devpts", "sysfs",
            "proc", "cgroup", "debugfs" };

    /**
     * Returns a list of mounted {@link StorageVolume}s Returned list always
     * includes a {@link StorageVolume} for
     * {@link Environment#getExternalStorageDirectory()}.
     *
     * @param includeUsb            if true, will include USB storages
     * @return list of mounted {@link StorageVolume}s
     */
    public List<StorageVolume> getStorages(final boolean includeUsb) {
        final Map<String, List<StorageVolume>> deviceVolumeMap = new HashMap<String, List<StorageVolume>>();

        // this approach considers that all storages are mounted in the same
        // non-root directory
        if (!STORAGES_ROOT.equals(File.separator)) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader("/proc/mounts"));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Log.d(TAG, line);
                    final StringTokenizer tokens = new StringTokenizer(line, " ");

                    final String device = tokens.nextToken();
                    // skipped devices that are not sdcard for sure
                    if (arrayContains(AVOIDED_DEVICES, device)) {
                        continue;
                    }

                    // should be mounted in the same directory to which
                    // the primary external storage was mounted
                    final String path = tokens.nextToken();
                    if (!path.startsWith(STORAGES_ROOT)) {
                        continue;
                    }

                    // skip directories that indicate tha volume is not a
                    // storage volume
                    if (pathContainsDir(path, AVOIDED_DIRECTORIES)) {
                        continue;
                    }

                    final String fileSystem = tokens.nextToken();
                    // don't add ones with non-supported filesystems
                    if (arrayContains(DISALLOWED_FILESYSTEMS, fileSystem)) {
                        continue;
                    }

                    final File file = new File(path);
                    // skip volumes that are not accessible
                    if (!file.canRead() || !file.canExecute()) {
                        continue;
                    }

                    List<StorageVolume> volumes = deviceVolumeMap.get(device);
                    if (volumes == null) {
                        volumes = new ArrayList<StorageVolume>(3);
                        deviceVolumeMap.put(device, volumes);
                    }

                    final StorageVolume volume = new StorageVolume(device, file, fileSystem);
                    final StringTokenizer flags = new StringTokenizer(tokens.nextToken(), ",");
                    while (flags.hasMoreTokens()) {
                        final String token = flags.nextToken();
                        if (token.equals("rw")) {
                            volume.mReadOnly = false;
                            break;
                        } else if (token.equals("ro")) {
                            volume.mReadOnly = true;
                            break;
                        }
                    }
                    volumes.add(volume);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        // ignored
                    }
                }
            }
        }

        // remove volumes that are the same devices
        boolean primaryStorageIncluded = false;
        final File externalStorage = Environment.getExternalStorageDirectory();
        final List<StorageVolume> volumeList = new ArrayList<StorageVolume>();
        for (final Map.Entry<String, List<StorageVolume>> entry : deviceVolumeMap.entrySet()) {
            final List<StorageVolume> volumes = entry.getValue();
            if (volumes.size() == 1) {
                // go ahead and add
                final StorageVolume v = volumes.get(0);
                final boolean isPrimaryStorage = v.file.equals(externalStorage);
                primaryStorageIncluded |= isPrimaryStorage;
                setTypeAndAdd(volumeList, v, includeUsb, isPrimaryStorage);
                continue;
            }
            final int volumesLength = volumes.size();
            for (int i = 0; i < volumesLength; i++) {
                final StorageVolume v = volumes.get(i);
                if (v.file.equals(externalStorage)) {
                    primaryStorageIncluded = true;
                    // add as external storage and continue
                    setTypeAndAdd(volumeList, v, includeUsb, true);
                    break;
                }
                // if that was the last one and it's not the default external
                // storage then add it as is
                if (i == volumesLength - 1) {
                    setTypeAndAdd(volumeList, v, includeUsb, false);
                }
            }
        }
        // add primary storage if it was not found
        if (!primaryStorageIncluded) {
            final StorageVolume defaultExternalStorage = new StorageVolume("", externalStorage, "UNKNOWN");
            defaultExternalStorage.mEmulated = Environment.isExternalStorageEmulated();
            defaultExternalStorage.mType = defaultExternalStorage.mEmulated ? StorageVolume.Type.INTERNAL
                    : StorageVolume.Type.EXTERNAL;
            defaultExternalStorage.mRemovable = Environment.isExternalStorageRemovable();
            defaultExternalStorage.mReadOnly = Environment.getExternalStorageState()
                    .equals(Environment.MEDIA_MOUNTED_READ_ONLY);
            volumeList.add(0, defaultExternalStorage);
        }
        return volumeList;
    }

    /**
     * Gets the storage.
     *
     * @param type the type
     * @return the storage
     */
    public  StorageVolume getStorage(StorageVolume.Type type) {
        List<StorageVolume> list_second = getStorages(false);
        for (Iterator<StorageVolume> iterator = list_second.iterator(); iterator.hasNext();) {
            StorageVolume storageVolume = (StorageVolume) iterator.next();
            if (storageVolume.mType == type) {
                boolean e = storageVolume.file != null && storageVolume.file.exists()
                        && storageVolume.file.list() != null;
                if (e) {
                    return storageVolume;
                }
            }
        }
        return null;
    }

    /**
     * Sets {@link StorageVolume.Type}, removable and emulated flags and adds to
     * volumeList
     *
     * @param volumeList
     *            List to add volume to
     * @param v
     *            volume to add to list
     * @param includeUsb
     *            if false, volume with type {@link StorageVolume.Type#USB} will
     *            not be added
     * @param asFirstItem
     *            if true, adds the volume at the beginning of the volumeList
     */
    private  void setTypeAndAdd(final List<StorageVolume> volumeList, final StorageVolume v,
                                final boolean includeUsb, final boolean asFirstItem) {
        final StorageVolume.Type type = resolveType(v);
        if (includeUsb || type != StorageVolume.Type.USB) {
            v.mType = type;
            if (v.file.equals(Environment.getExternalStorageDirectory())) {
                v.mRemovable = Environment.isExternalStorageRemovable();
            } else {
                v.mRemovable = type != StorageVolume.Type.INTERNAL;
            }
            v.mEmulated = type == StorageVolume.Type.INTERNAL;
            if (asFirstItem) {
                volumeList.add(0, v);
            } else {
                volumeList.add(v);
            }
        }
    }

    /**
     * Resolved {@link StorageVolume} type.
     *
     * @param v            {@link StorageVolume} to resolve type for
     * @return {@link StorageVolume} type
     */
    private  StorageVolume.Type resolveType(final StorageVolume v) {
        if (v.file.equals(Environment.getExternalStorageDirectory()) && Environment.isExternalStorageEmulated()) {
            return StorageVolume.Type.INTERNAL;
        } else if (containsIgnoreCase(v.file.getAbsolutePath(), "usb")) {
            return StorageVolume.Type.USB;
        } else {
            return StorageVolume.Type.EXTERNAL;
        }
    }

    /**
     * Checks whether the array contains object.
     *
     * @param <T> the generic type
     * @param array            Array to check
     * @param object            Object to find
     * @return true, if the given array contains the object
     */
    private  <T> boolean arrayContains(T[] array, T object) {
        for (final T item : array) {
            if (item.equals(object)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the path contains one of the directories
     *
     * For example, if path is /one/two, it returns true input is "one" or
     * "two". Will return false if the input is one of "one/two", "/one" or
     * "/two"
     *
     * @param path
     *            path to check for a directory
     * @param dirs
     *            directories to find
     * @return true, if the path contains one of the directories
     */
    private  boolean pathContainsDir(final String path, final String[] dirs) {
        final StringTokenizer tokens = new StringTokenizer(path, File.separator);
        while (tokens.hasMoreElements()) {
            final String next = tokens.nextToken();
            for (final String dir : dirs) {
                if (next.equals(dir)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks ifString contains a search String irrespective of case, handling.
     * Case-insensitivity is defined as by
     * {@link String#equalsIgnoreCase(String)}.
     *
     * @param str
     *            the String to check, may be null
     * @param searchStr
     *            the String to find, may be null
     * @return true if the String contains the search String irrespective of
     *         case or false if not or {@code null} string input
     */
    public  boolean containsIgnoreCase(final String str, final String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        final int len = searchStr.length();
        final int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (str.regionMatches(true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Represents storage volume information.
     */
    public static  final class StorageVolume {

        /**
         * Represents {@link StorageVolume} type.
         */
        public enum Type {
            /**
             * Device built-in internal storage. Probably points to
             * {@link Environment#getExternalStorageDirectory()}
             */
            INTERNAL,

            /**
             * External storage. Probably removable, if no other
             * {@link StorageVolume} of type {@link #INTERNAL} is returned by
             * {@link StorageHelper#getStorages(boolean)}, this might be
             * pointing to {@link Environment#getExternalStorageDirectory()}
             */
            EXTERNAL,

            /** Removable usb storage. */
            USB
        }

        /** Device name. */
        public final String device;

        /** Points to mount point of this device. */
        public final File file;

        /** File system of this device. */
        public final String fileSystem;

        /** if true, the storage is mounted as read-only. */
        private boolean mReadOnly;

        /** If true, the storage is removable. */
        private boolean mRemovable;

        /** If true, the storage is emulated. */
        private boolean mEmulated;

        /** Type of this storage. */
        private Type mType;

        /**
         * Instantiates a new storage volume.
         *
         * @param device the device
         * @param file the file
         * @param fileSystem the file system
         */
        StorageVolume(String device, File file, String fileSystem) {
            this.device = device;
            this.file = file;
            this.fileSystem = fileSystem;
        }

        /**
         * Returns type of this storage.
         *
         * @return Type of this storage
         */
        public Type getType() {
            return mType;
        }

        /**
         * Returns true if this storage is removable.
         *
         * @return true if this storage is removable
         */
        public boolean isRemovable() {
            return mRemovable;
        }

        /**
         * Returns true if this storage is emulated.
         *
         * @return true if this storage is emulated
         */
        public boolean isEmulated() {
            return mEmulated;
        }

        /**
         * Returns true if this storage is mounted as read-only.
         *
         * @return true if this storage is mounted as read-only
         */
        public boolean isReadOnly() {
            return mReadOnly;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((file == null) ? 0 : file.hashCode());
            return result;
        }

        /**
         * Returns true if the other object is StorageHelper and it's
         * {@link #file} matches this one's.
         *
         * @param obj the obj
         * @return true, if successful
         * @see Object#equals(Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StorageVolume other = (StorageVolume) obj;
            if (file == null) {
                return other.file == null;
            }
            return file.equals(other.file);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return file.getAbsolutePath() + (mReadOnly ? " ro " : " rw ") + mType + (mRemovable ? " R " : "")
                    + (mEmulated ? " E " : "") + fileSystem;
        }
    }
}
