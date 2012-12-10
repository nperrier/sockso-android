package com.pugh.sockso.android.widget;

import android.database.Cursor;
import android.util.SparseIntArray;
import android.widget.SectionIndexer;

/**
 * A helper class for adapters that implement the SectionIndexer interface.
 * <p/>
 * If the items in the adapter are sorted by according to specified String types,
 * then this class provides a way to do fast indexing of large lists using binary search.
 * <p/>
 * It caches the indices that have been determined through the binary search and also
 * invalidates the cache if changes occur in the cursor.
 * <p/>
 * If the cursor changes, then the adapter must call {@link #setCursor} to invalidate the cached sections indexes
 */
public class MusicItemIndexer implements SectionIndexer {

    /**
     * Cursor that is used by the adapter of the list view.
     */
    protected Cursor mDataCursor;

    /**
     * The index of the cursor column that this list is sorted on.
     */
    protected int mColumnIndex;

    /**
     * The string of characters that make up the indexing sections.
     */
    protected CharSequence[] mSections;

    /**
     * Cached length of the section array.
     */
    private int mSectionsLength;

    /**
     * This contains a cache of the computed indices so far. 
     * It will get reset whenever the dataset changes or the cursor changes.
     */
    private SparseIntArray mSectionsMap;

    /**
     * Constructs the indexer.
     * 
     * @param cursor the Cursor containing the data set
     * @param sortedColumnIndex the column number in the cursor that is sorted alphabetically
     * @param sections string array containing the sections.
     */
    public MusicItemIndexer(Cursor cursor, int sortedColumnIndex, CharSequence[] sections) {

        mDataCursor     = cursor;
        mColumnIndex    = sortedColumnIndex;

        mSections       = sections;
        mSectionsLength = sections.length;
        
        mSectionsMap    = new SparseIntArray(mSectionsLength);
    }

    /**
     * Returns the section array constructed from the section array provided in the constructor.
     * 
     * @return the section array
     */
    public Object[] getSections() {
        return mSections;
    }

    /**
     * Sets a new cursor as the data set and resets the cache of indices.
     * 
     * @param cursor the new cursor to use as the data set
     */
    public void setCursor(Cursor cursor) {

        mDataCursor = cursor;
        mSectionsMap.clear();
    }


    /**
     * Performs a binary search or cache lookup to find the first row that
     * matches a given section's starting letter.
     * 
     * @param sectionIndex the section to search for
     * @return the row index of the first occurrence, or the nearest next letter.
     *         For instance, if searching for "T" and no "T" is found, then the first
     *         row starting with "U" or any higher letter is returned. 
     *         If there is no data following "T" at all, then the list size is returned.
     */
    public int getPositionForSection(int sectionIndex) {

        final SparseIntArray sectionsMap = mSectionsMap;
        final Cursor cursor = mDataCursor;

        if (cursor == null || mSections.length == 0) {
            return 0;
        }

        // Check bounds
        if (sectionIndex <= 0) {
            return 0;
        }
        
        // If the sectionIndex is greater than the length of the sections, set it to the end
        if (sectionIndex >= mSectionsLength) {
            sectionIndex = mSectionsLength - 1;
        }

        int savedCursorPos = cursor.getPosition();

        int count = cursor.getCount();
        int start = 0;
        int end = count;
        int pos;

        CharSequence section = mSections[sectionIndex];
        int key = sectionIndex;
        
        // Check map
        if (Integer.MIN_VALUE != (pos = sectionsMap.get(key, Integer.MIN_VALUE))) {
            
            // Is it approximate? Using negative value to indicate that it's
            // an approximation and positive value when it is the accurate position.
            if (pos < 0) {
                pos = -pos;
                end = pos;
            }
            else {
                // Not approximate, this is the confirmed start of section, return it
                return pos;
            }
        }

        // Do we have the position of the previous section?
        if (sectionIndex > 0) {
            
            int prevLetterPos = mSectionsMap.get(sectionIndex - 1, Integer.MIN_VALUE);
            
            if (prevLetterPos != Integer.MIN_VALUE) {
                start = Math.abs(prevLetterPos);
            }
        }

        // Now that we have a possibly optimized start and end, let's binary search

        pos = (end + start) / 2;

        while (pos < end) {
            // Get section at pos
            cursor.moveToPosition(pos);
            String secName = cursor.getString(mColumnIndex);
            
            if (secName == null) {
                if (pos == 0) {
                    break;
                }
                else {
                    pos--;
                    continue;
                }
            }
            
            int diff = compare(secName, section.toString());
            
            // They aren't the same
            if (diff != 0) {
                
                if (diff < 0) {
                    // New start position
                    start = pos + 1;
                    
                    // If we're at the end of the list of sections, since the list is sorted,
                    // then the position for this section is the end of the list
                    if (start >= count) {
                        pos = count;
                        break;
                    }
                }
                else {
                    // New end position
                    end = pos;
                }
            }
            else {
                // They're the same, but that doesn't mean it's the start
                if (start == pos) {
                    // Found the start, we're done :)
                    break;
                }
                else {
                    // Need to go further lower to find the starting row (we could be in the middle)
                    end = pos;
                }
            }
            
            // Get the new midpoint
            pos = (start + end) / 2;
        }
        
        // Remember where we found the section
        mSectionsMap.put(key, pos);
        
        // Set the cursor back to its original position
        cursor.moveToPosition(savedCursorPos);
        
        return pos;
    }

    /**
     * Returns the section index for a given position in the list by querying the item
     * and comparing it with all items in the section array.
     */
    public int getSectionForPosition(int position) {

        int savedCursorPos = mDataCursor.getPosition();
        mDataCursor.moveToPosition(position);
        
        String curName = mDataCursor.getString(mColumnIndex);
        mDataCursor.moveToPosition(savedCursorPos);

        // Linear search, as there are only a few items in the section index
        // Could speed this up later if it actually gets used.
        for (int i = 0; i < mSectionsLength; i++) {
            
            CharSequence section = mSections[i];
            
            if (curName.equalsIgnoreCase(section.toString())) {
                return i;
            }
        }

        return 0; // Don't recognize the letter - falls under zero'th section
    }
    
    // Order of the section array used in the constructor
    private int compare(String left, String right) {
        
        int leftIndex  = 0;
        int rightIndex = 0;
        
        boolean foundLeft  = false;
        boolean foundRight = false;
        
        for (int i = 0; i < mSectionsLength; i++) {
            
            String section = mSections[i].toString();
            
            if (!foundLeft && left.equalsIgnoreCase(section)) {
                leftIndex = i;
                foundLeft = true;
            }
            
            if (!foundRight && right.equalsIgnoreCase(section)) {
                rightIndex = i;
                foundRight = true;
            }
         
            if (foundLeft && foundRight) {
                break;
            }
        }
        
        return (leftIndex - rightIndex);
    }
}
