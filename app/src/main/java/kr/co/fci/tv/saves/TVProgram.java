/********************************************************************************************************************/
/**
 *  @skip   $Id: TVProgram.java 1920 2017-11-29 07:06:41Z eddy.lee $
 *  @file   TVProgram.java
 *  @brief  TV Programs table.
 *  @date   2015/05/21 FCI elliot create.
 *
 *  ALL Rights Reserved, Copyright(C) FCI 2015
 */
/********************************************************************************************************************/
package kr.co.fci.tv.saves;

import android.net.Uri;
import android.provider.BaseColumns;

public final class TVProgram {
    public static final String AUTHORITY = "kr.co.fci.tv.saves.TVProgram";

    // This class cannot be instantiated
    private TVProgram() {}

    /**
     * TV Programs table
     */
    public static final class Programs implements BaseColumns {
        // This class cannot be instantiated
        private Programs() {}
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/programs");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fci.programs";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.fci.programs";

        /**
         * The default sort order for this table
         */
        public static final String SORT_ORDER_BY_ID = "serviceid ASC";//"type ASC, servicename ASC";//"title DESC";
        public static final String SORT_ORDER_BY_NAME = "servicename ASC";//"type ASC, servicename ASC";//"title DESC";

        /**
         * service ID
         * <P>Type: TEXT</P>
         */
        public static final String SERVICEID = "serviceid";
        
        /**
         * The name of the service
         * <P>Type: TEXT</P>
         */
        public static final String SERVICENAME = "servicename";

        /**
         * The service frequency
         * <P>Type: INTEGER</P>
         */
        public static final String FREQ = "freq";
        
        /**
         * The service bandwidth
         * <P>Type: TEXT</P>
         */
        public static final String FREE = "freechannel";

        /**
         * The service type: 1 - TV 2 - Radio
         * <P>Type: INTEGER</P>
         */
        public static final String TYPE = "type";
        
        /**
         * If the service is favorite : 0 - false 1 - true
         * <P>Type: INTEGER</P>
         */
        public static final String FAV = "favorite";
        
        /**
         * If the service is encrypt : 0 - false 1 - true
         * <P>Type: INTEGER</P>
         */
        //public static final String ENCRYPT = "encrypt";
        public static final String MTV = "mtv";

        /**
         * video format
         * <P>Type: INTEGER</P>
         */
        public static final String VIDFORM = "videoformat";

        /**
         * audio format
         * <P>Type: INTEGER</P>
         */
        public static final String AUDFORM = "audioformat";

        /**
         * remote control key ID
         * <P>Type: INTEGER</P>
         */
        public static final String REMOTEKEY = "remotekey";

        /**
         * SI's service ID
         * <P>Type: INTEGER</P>
         */
        public static final String SVCNUM = "servicenumber";
    }
}
