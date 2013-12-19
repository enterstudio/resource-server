/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.resources.provisioning.update;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.osiam.resources.converter.PhoneNumberConverter;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.storage.entities.PhoneNumberEntity;
import org.osiam.storage.entities.UserEntity;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

/**
 * The PhoneNumberUpdater provides the functionality to update the {@link PhoneNumberEntity} of a UserEntity
 */
@Service
class PhoneNumberUpdater {

    @Inject
    private PhoneNumberConverter phoneNumberConverter;

    /**
     * updates (adds new, delete, updates) the {@link PhoneNumberEntity}'s of the given {@link UserEntity} based on the
     * given List of PhoneNumber's
     *
     * @param phoneNumbers
     *            list of PhoneNumber's to be deleted, updated or added
     * @param userEntity
     *            user who needs to be updated
     * @param attributes
     *            all {@link PhoneNumberEntity}'s will be deleted if this Set contains 'phoneNumbers'
     */
    void update(List<MultiValuedAttribute> phoneNumbers, UserEntity userEntity, Set<String> attributes) {

        if (attributes.contains("phoneNumbers")) {
            userEntity.removeAllPhoneNumbers();
        }

        if (phoneNumbers != null) {
            for (MultiValuedAttribute scimPhoneNumber : phoneNumbers) {
                PhoneNumberEntity phoneNumberEntity = phoneNumberConverter.fromScim(scimPhoneNumber);
                userEntity.removePhoneNumber(phoneNumberEntity); // we always have to remove the phoneNumber in case
                // the active flag has changed
                if (Strings.isNullOrEmpty(scimPhoneNumber.getOperation())
                        || !scimPhoneNumber.getOperation().equalsIgnoreCase("delete")) {

                    // TODO primary is not implemented yet. If it is see EmailUpdater how to implement it here
                    userEntity.addPhoneNumber(phoneNumberEntity);
                }
            }
        }
    }

}
