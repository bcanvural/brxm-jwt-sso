package com.bloomreach.ps.beans;
/*
 * Copyright 2014-2019 Hippo B.V. (http://www.onehippo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

@HippoEssentialsGenerated(internalName = "brxmjwtsso:bannerdocument")
@Node(jcrType = "brxmjwtsso:bannerdocument")
public class Banner extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "brxmjwtsso:title")
    public String getTitle() {
        return getSingleProperty("brxmjwtsso:title");
    }

    @HippoEssentialsGenerated(internalName = "brxmjwtsso:content")
    public HippoHtml getContent() {
        return getHippoHtml("brxmjwtsso:content");
    }

    @HippoEssentialsGenerated(internalName = "brxmjwtsso:image")
    public HippoGalleryImageSet getImage() {
        return getLinkedBean("brxmjwtsso:image", HippoGalleryImageSet.class);
    }

    @HippoEssentialsGenerated(internalName = "brxmjwtsso:link")
    public HippoBean getLink() {
        return getLinkedBean("brxmjwtsso:link", HippoBean.class);
    }
}
