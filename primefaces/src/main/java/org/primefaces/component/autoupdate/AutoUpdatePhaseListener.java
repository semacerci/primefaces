/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.autoupdate;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.primefaces.context.PrimeRequestContext;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

public class AutoUpdatePhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void afterPhase(PhaseEvent event) {

    }

    @Override
    public void beforePhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();
        if (!context.isPostback() || PrimeRequestContext.getCurrentInstance(context).isIgnoreAutoUpdate()) {
            return;
        }

        Map<String, String> infos = AutoUpdateListener.getAutoUpdateComponentInfos(context);
        if (infos != null && !infos.isEmpty()) {
            for (Map.Entry<String, String> entries : infos.entrySet()) {
                String clientId = entries.getKey();
                String on = entries.getValue();

                if (on != null) {
                    String update = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_UPDATE_PARAM);
                    if (LangUtils.isBlank(update) || !update.contains("@obs(" + on + ")")) {
                        continue;
                    }
                }

                if (!context.getPartialViewContext().getRenderIds().contains(clientId)) {
                    context.getPartialViewContext().getRenderIds().add(clientId);
                }
            }
        }
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

}
