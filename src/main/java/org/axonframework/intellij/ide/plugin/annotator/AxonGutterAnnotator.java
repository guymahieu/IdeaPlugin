package org.axonframework.intellij.ide.plugin.annotator;

import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.psi.PsiElement;
import org.axonframework.intellij.ide.plugin.handler.AnnotationTypes;
import org.axonframework.intellij.ide.plugin.handler.EventHandler;
import org.axonframework.intellij.ide.plugin.handler.HandlerProviderManager;
import org.axonframework.intellij.ide.plugin.publisher.EventPublisher;
import org.axonframework.intellij.ide.plugin.publisher.PublisherProviderManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class shows an icon in the gutter when an Axon annotation is found. The icon can be used to navigate to all
 * classes that handle the event.
 */
public class AxonGutterAnnotator implements Annotator {

    private static final Icon AxonIconIn = IconLoader.getIcon("/icons/axon_into.png"); // 16x16
    private static final Icon AxonIconOut = IconLoader.getIcon("/icons/axon_publish.png"); // 16x16

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        final PublisherProviderManager publisherManager = PublisherProviderManager.getInstance(element.getProject());
        final HandlerProviderManager handlerManager = HandlerProviderManager.getInstance(element.getProject());
        final EventPublisher publisher = publisherManager.resolveEventPublisher(element);
        final EventHandler handler = handlerManager.resolveEventHandler(element);
        if (publisher != null) {
            NotNullLazyValue<Collection<? extends PsiElement>> targetResolver = new NotNullLazyValue<Collection<? extends PsiElement>>() {
                @NotNull
                @Override
                protected Collection<? extends PsiElement> compute() {
                    Set<EventHandler> handlers = handlerManager.getRepository().findHandlers(publisher.getPublishedType());
                    Collection<PsiElement> destinations = new HashSet<PsiElement>();
                    for (EventHandler eventHandler : handlers) {
                        destinations.add(eventHandler.getElementForAnnotation());
                    }
                    return destinations;
                }
            };
            Annotation gutterIconForPublisher = createGutterIconForPublisher(element, holder, targetResolver);
            if (targetResolver.getValue().isEmpty()) {
                addCreateEventHandlerQuickFixes(publisher, gutterIconForPublisher);
            }
        }

        if (handler != null) {
            createGutterIconForHandler(element, holder, new NotNullLazyValue<Collection<? extends PsiElement>>() {
                @NotNull
                @Override
                protected Collection<? extends PsiElement> compute() {
                    Collection<EventPublisher> publishers = publisherManager.getRepository()
                            .getPublishersFor(handler.getHandledType());
                    Collection<PsiElement> publishLocations = new ArrayList<PsiElement>();
                    for (EventPublisher eventPublisher : publishers) {
                        publishLocations.add(eventPublisher.getPsiElement());
                    }
                    return publishLocations;
                }
            });
        }
    }

    private void addCreateEventHandlerQuickFixes(EventPublisher publisher, Annotation gutterIconForPublisher) {
        gutterIconForPublisher.registerFix(new CreateEventHandlerQuickfix(publisher.getPublishedType(), AnnotationTypes.EVENT_HANDLER));
        gutterIconForPublisher.registerFix(new CreateEventHandlerQuickfix(publisher.getPublishedType(), AnnotationTypes.EVENT_SOURCING_HANDLER));
        gutterIconForPublisher.registerFix(new CreateEventHandlerQuickfix(publisher.getPublishedType(), AnnotationTypes.SAGA_EVENT_HANDLER));
    }

    private static Annotation createGutterIconForHandler(PsiElement psiElement, AnnotationHolder holder,
                                                         NotNullLazyValue<Collection<? extends PsiElement>> targetResolver) {
        return NavigationGutterIconBuilder.create(AxonIconIn)
                .setEmptyPopupText("No publishers found for this event")
                .setTargets(targetResolver)
                .setPopupTitle("Publishers")
                .setCellRenderer(new ContainingMethodCellRenderer())
                .setTooltipText("Navigate to the publishers of this event")
                .install(holder, psiElement);
    }

    private static Annotation createGutterIconForPublisher(PsiElement psiElement, AnnotationHolder holder,
                                                           NotNullLazyValue<Collection<? extends PsiElement>> targetResolver) {
        return NavigationGutterIconBuilder.create(AxonIconOut)
                .setEmptyPopupText("No handlers found for this event")
                .setTargets(targetResolver)
                .setPopupTitle("Event Handlers")
                .setCellRenderer(new ContainingMethodCellRenderer())
                .setTooltipText("Navigate to the handlers for this event")
                .install(holder, psiElement);
    }

}