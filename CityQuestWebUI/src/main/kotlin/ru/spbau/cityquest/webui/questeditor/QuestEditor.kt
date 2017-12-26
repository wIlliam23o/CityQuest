package ru.spbau.cityquest.webui.questeditor

import google.maps.KtGoogleMap
import google.maps.KtMarker
import google.maps.MapOptions
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import kotlin.browser.document

const val defaultTitle : String = "Lorem ipsum dolor sit amet"
const val defaultDesc : String = "All embrace me\n" +
        "It's my time to rule at last\n" +
        "Fifteen years have I been waiting\n" +
        "To sit upon my throne\n" +
        "No allegiance\n" +
        "I will swear no oath\n" +
        "Crowned by god not by the church\n" +
        "As my power is divine\n" +
        "They thought I was too young to rule the land\n" +
        "Just as they failed to understand\n" +
        "Born to rule\n" +
        "My time has come\n" +
        "I was chosen by heaven\n" +
        "Say my name when you pray\n" +
        "To the skies\n" +
        "See Carolus rise\n" +
        "With the lord my protector\n" +
        "Make them bow to my will\n" +
        "To the skies\n" +
        "See Carolus rise\n"


class QuestEditor(mapOptions: MapOptions) {
    class DocumentNodes {
        val map = document.getElementById("map") as HTMLElement?
        val editGPSPointTitleEditInput = document.getElementById("edit-gps-point-title-edit-input") as HTMLElement?
        val editGPSPointDescEditInput = document.getElementById("edit-gps-point-desc-edit-input")  as HTMLElement?
        val saveChanges = document.getElementById("save-changes") as HTMLElement?
        val willAppear = document.getElementById("will-appear") as HTMLElement?
        val stateViewer = document.getElementById("state") as HTMLElement?
        val editGPSPoint = document.getElementById("edit-gps-point") as HTMLElement?
        val questPointsList = document.getElementById("quest-points-list") as HTMLElement?
    }

    val documentNodes : DocumentNodes = DocumentNodes()

    data class CurrentEdit(val editIndex: Int) {
        fun isNothing() : Boolean = editIndex == -2
        fun isNew() : Boolean = editIndex == -1
    }

    val editNothing : CurrentEdit = CurrentEdit(-2)
    val editNew : CurrentEdit = CurrentEdit(-1)

    val map : KtGoogleMap = KtGoogleMap(documentNodes.map, mapOptions)

    fun onClickListener(event : Event) {
        val questPoint = GPSQuestPoint(questPoints.nextId, getCurrentEditTitle(), getCurrentEditDesc(), js("event.latLng"))
        questPoint.marker = KtMarker(questPoint.latLng, map)
        questPoint.marker?.set("label", "${questPoints.size + 1}")
        questPoints.addPoint(questPoint)
        editorState.switchState(QuestEditorStateManager.QuestEditorState.VIEW)
    }

    var mapOnClickListener : Any? = null

    val questPoints = QuestPointStorage(this)

    var editorState : QuestEditorStateManager = QuestEditorStateManager(this)

    init {
        editorState.switchState(QuestEditorStateManager.QuestEditorState.VIEW)
    }

    var currentEdit : CurrentEdit = editNothing
        set(value) {
            field = value
            if (value.isNothing()) {
                documentNodes.editGPSPointTitleEditInput?.innerHTML = ""
                documentNodes.editGPSPointDescEditInput?.innerHTML = ""
            } else if (value.isNew()) {
                val titleVal = document.createAttribute("value")
                titleVal.value = defaultTitle
                documentNodes.editGPSPointTitleEditInput?.attributes?.setNamedItem(titleVal)
                documentNodes.editGPSPointDescEditInput?.innerHTML = defaultDesc
                documentNodes.saveChanges?.style?.visibility = "hidden"
            } else {
                documentNodes.saveChanges?.style?.visibility = "visible"
                val point = questPoints.getQuestPointById(value.editIndex)
                val titleVal = document.createAttribute("value")
                titleVal.value = point.title
                documentNodes.editGPSPointTitleEditInput?.attributes?.setNamedItem(titleVal)
                documentNodes.editGPSPointDescEditInput?.innerHTML = point.desc
            }
        }

    fun getCurrentEditTitle() : String = documentNodes.editGPSPointTitleEditInput?.attributes?.getNamedItem("value")?.value ?: ""

    fun getCurrentEditDesc() : String = documentNodes.editGPSPointDescEditInput?.innerHTML ?: ""

    @JsName("newQuestPoint")
    fun newQuestPoint() {
        currentEdit = editNew
        editorState.switchState(QuestEditorStateManager.QuestEditorState.EDIT_QUEST_POINT)
    }

    @JsName("cancelNewPoint")
    fun cancelNewPoint() {
        editorState.switchState(QuestEditorStateManager.QuestEditorState.VIEW)
    }

    @JsName("placeMarker")
    fun placeMarker() {
        editorState.switchState(QuestEditorStateManager.QuestEditorState.PLACE_MARKER)
    }

    @JsName("saveChanges")
    fun saveChanges() {
        editorState.switchState(QuestEditorStateManager.QuestEditorState.VIEW)
    }
}