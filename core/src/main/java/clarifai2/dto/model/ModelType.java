package clarifai2.dto.model;

import clarifai2.dto.model.output_info.*;
import clarifai2.dto.prediction.*;
import clarifai2.exception.ClarifaiException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public enum ModelType {
  CONCEPT(
      "concept",
      "concepts",
      ConceptOutputInfo.class,
      Concept.class
  ),
  EMBEDDING(
      "embed",
      "embeddings",
      EmbeddingOutputInfo.class,
      Embedding.class
  ),
  COLOR(
      "color",
      "colors",
      ColorOutputInfo.class,
      Color.class
  ),
  DEMOGRAPHICS(
      "facedetect",
      "regions",
      DemographicsOutputInfo.class,
      Region.class
  ),
  FACE_DETECTION(
      "facedetect",
      "regions",
      FaceDetectionOutputInfo.class,
      FaceDetection.class
  ),
  FOCUS(
      "blur",
      "focus",
      FocusOutputInfo.class,
      Focus.class
  ),
  CLUSTER(
      "cluster",
      "clusters",
      ClusterOutputInfo.class,
      Cluster.class
  ),;

  @NotNull private final String typeName;
  @NotNull private final String dataArrayName;
  @NotNull private final Class<? extends OutputInfo> infoType;
  @NotNull private final Class<? extends Prediction> tagType;

  ModelType(
      @NotNull String typeName,
      @NotNull String dataArrayName,
      @NotNull Class<? extends OutputInfo> infoType,
      @NotNull Class<? extends Prediction> tagType
  ) {
    this.typeName = typeName;
    this.dataArrayName = dataArrayName;
    this.infoType = infoType;
    this.tagType = tagType;
  }

  @NotNull public String typeName() {
    return typeName;
  }

  @NotNull public Class<? extends OutputInfo> infoType() {
    return infoType;
  }

  @NotNull public Class<? extends Prediction> predictionType() {
    return tagType;
  }

  @NotNull public static ModelType determineFromDataRoot(@NotNull JsonObject dataRoot) {
    for (final ModelType value : values()) {
      if (dataRoot.has(value.dataArrayName)) {
        if (value.dataArrayName.equalsIgnoreCase("regions")) {
          // fixes ambiguation error between Demographics and FaceDetection model. If confused, see Postman, and notice
          // that the way the model is determined is ambiguous in this case.
          if (dataRoot.getAsJsonArray("regions").size() == 0) {
            return UNKNOWN;
          }
          // even more amibiguation.
          if (dataRoot.has("focus")) {
            return FOCUS;
          }
          if (dataRoot.getAsJsonArray("regions").get(0).getAsJsonObject().has("data")) {
            return DEMOGRAPHICS;
          } else {
            return FACE_DETECTION;
          }
        }
        return value;
      }
    }
    return UNKNOWN;
  }

  @NotNull public static ModelType determineFromOutputInfoRoot(@NotNull JsonElement outputInfoRoot) {
    final String type = outputInfoRoot.getAsJsonObject().get("type").getAsString();
    for (final ModelType value : values()) {
      if (value.typeName().equals(type)) {
        return value;
      }
    }
    return UNKNOWN;
  }
}
