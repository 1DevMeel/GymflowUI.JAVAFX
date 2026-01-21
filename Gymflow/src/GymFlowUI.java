import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Optional;

public class GymFlowUI extends Application {

    // Backend Connection
    GymManagement db = new GymManagement();

    // Colors
    private final String BG_DARK = "#121212";
    private final String CARD_BG = "#1E1E1E";
    private final String ACCENT = "#3D5AFE";
    private final String TEXT_WHITE = "#FFFFFF";

    private BorderPane root;

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_DARK + ";");

        // UI Components
        root.setLeft(createSidebar(root));
        root.setCenter(createDashboard());

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("GymFlow - Admin Panel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createSidebar(BorderPane root) {
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: " + CARD_BG + ";");
        sidebar.setPrefWidth(220);

        Label logo = new Label("GymFlow");
        logo.setTextFill(Color.web(ACCENT));
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Button btnDash = new Button("Dashboard");
        styleButton(btnDash);
        btnDash.setOnAction(e -> root.setCenter(createDashboard()));

        Button btnAdd = new Button("Add Member");
        styleButton(btnAdd);
        btnAdd.setOnAction(e -> root.setCenter(createAddMemberView()));
        styleButton(btnAdd);

        sidebar.getChildren().addAll(logo, new Separator(), btnDash, btnAdd);
        return sidebar;
    }

    private VBox createDashboard() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label title = new Label("Member List (Live Data)");
        title.setTextFill(Color.web(TEXT_WHITE));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        // Member List Display
        TableView<Member> memberTable = new TableView<>();
        memberTable.setPrefWidth(900);
        memberTable.setPlaceholder(new Label("No members to display."));
        memberTable.setStyle("-fx-background-color: " + CARD_BG + "; -fx-text-fill: " + TEXT_WHITE + ";");

        TableColumn<Member, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setPrefWidth(150);

        TableColumn<Member, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        idCol.setPrefWidth(80);

        TableColumn<Member, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClass().getSimpleName()));
        typeCol.setPrefWidth(120);

        TableColumn<Member, String> feeCol = new TableColumn<>("Fee (Rs.)");
        feeCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getNetFee())));
        feeCol.setPrefWidth(100);

        // Actions Column
        TableColumn<Member, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(350);

        Callback<TableColumn<Member, Void>, TableCell<Member, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Member, Void> call(final TableColumn<Member, Void> param) {
                return new TableCell<>() {
                    private final Button btnEdit = new Button("Edit");
                    private final Button btnDiscount = new Button("Discount");
                    private final Button btnDelete = new Button("Delete");
                    private final HBox pane = new HBox(10, btnEdit, btnDiscount, btnDelete);

                    {
                        btnEdit.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        btnDiscount.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black;");
                        btnDelete.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");

                        btnEdit.setOnAction(event -> {
                            Member member = getTableView().getItems().get(getIndex());
                            showEditDialog(member);
                            getTableView().refresh();
                        });

                        btnDiscount.setOnAction(event -> {
                            Member member = getTableView().getItems().get(getIndex());
                            showDiscountDialog(member);
                            getTableView().refresh();
                        });

                        btnDelete.setOnAction(event -> {
                            Member member = getTableView().getItems().get(getIndex());
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + member.getName() + "?", ButtonType.YES, ButtonType.NO);
                            alert.showAndWait();
                            if (alert.getResult() == ButtonType.YES) {
                                db.removeMember(member);
                                getTableView().getItems().remove(member);
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        };
        actionsCol.setCellFactory(cellFactory);

        memberTable.getColumns().addAll(nameCol, idCol, typeCol, feeCol, actionsCol);
        ObservableList<Member> memberData = FXCollections.observableArrayList(db.getAllMembers());
        memberTable.setItems(memberData);

        content.getChildren().addAll(title, memberTable);
        return content;
    }

    private void styleButton(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER-LEFT;");
        btn.setPrefWidth(200);
    }

    private VBox createAddMemberView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: " + BG_DARK + ";");

        Label title = new Label("Add New Member");
        title.setTextFill(Color.web(TEXT_WHITE));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        TextField nameField = new TextField();
        nameField.setPromptText("Member Name");
        nameField.setStyle("-fx-background-color: " + CARD_BG + "; -fx-text-fill: " + TEXT_WHITE + ";");

        TextField idField = new TextField();
        idField.setPromptText("Member ID");
        idField.setStyle("-fx-background-color: " + CARD_BG + "; -fx-text-fill: " + TEXT_WHITE + ";");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.setStyle("-fx-background-color: " + CARD_BG + "; -fx-text-fill: " + TEXT_WHITE + ";");

        ChoiceBox<String> memberTypeChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList("Premium Member", "Student Member"));
        memberTypeChoiceBox.setValue("Premium Member"); // Default selection
        memberTypeChoiceBox.setStyle("-fx-background-color: " + CARD_BG + "; -fx-text-fill: " + TEXT_WHITE + ";");

        // Additional fields for PremiumMember
        CheckBox hasPersonalTrainerCheckBox = new CheckBox("Has Personal Trainer");
        hasPersonalTrainerCheckBox.setTextFill(Color.web(TEXT_WHITE));
        hasPersonalTrainerCheckBox.setStyle("-fx-background-color: " + CARD_BG + ";");

        // Additional fields for StudentMember
        TextField universityField = new TextField();
        universityField.setPromptText("University Name");
        universityField.setStyle("-fx-background-color: " + CARD_BG + "; -fx-text-fill: " + TEXT_WHITE + ";");

        // Dynamic visibility based on member type selection
        hasPersonalTrainerCheckBox.setVisible(true);
        universityField.setVisible(false);

        memberTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isPremium = newVal.equals("Premium Member");
            hasPersonalTrainerCheckBox.setVisible(isPremium);
            universityField.setVisible(!isPremium);
        });

        Button saveButton = new Button("Save Member");
        styleButton(saveButton);
        saveButton.setOnAction(e -> {
            String name = nameField.getText();
            String id = idField.getText();
            String phone = phoneField.getText();
            String memberType = memberTypeChoiceBox.getValue();

            if (name.isEmpty() || id.isEmpty() || phone.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter all required fields.");
                return;
            }

            Member newMember;
            if (memberType.equals("Premium Member")) {
                newMember = new PremiumMember(name, id, phone, hasPersonalTrainerCheckBox.isSelected());
            } else { // Student Member
                String universityName = universityField.getText();
                if (universityName.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter university name for student member.");
                    return;
                }
                newMember = new StudentMember(name, id, phone, universityName);
            }
            db.addMember(newMember);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Member added successfully!");

            // Clear fields and refresh dashboard
            nameField.clear();
            idField.clear();
            phoneField.clear();
            hasPersonalTrainerCheckBox.setSelected(false);
            universityField.clear();
            root.setCenter(createDashboard()); // Refresh the dashboard view
        });

        content.getChildren().addAll(title, nameField, idField, phoneField, memberTypeChoiceBox, hasPersonalTrainerCheckBox, universityField, saveButton);
        return content;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showDiscountDialog(Member member) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(member.getDiscount()));
        dialog.setTitle("Apply Discount");
        dialog.setHeaderText("Enter discount amount for " + member.getName());
        dialog.setContentText("Discount (Rs.):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                member.setDiscount(amount);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number.");
            }
        });
    }

    private void showEditDialog(Member member) {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Edit Member");
        dialog.setHeaderText("Edit details for " + member.getName());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(member.getName());
        TextField phoneField = new TextField(member.getPhone());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);

        // Specific fields
        CheckBox hasTrainerBox = new CheckBox("Has Personal Trainer");
        TextField uniField = new TextField();

        if (member instanceof PremiumMember) {
            hasTrainerBox.setSelected(((PremiumMember) member).hasPersonalTrainer());
            grid.add(hasTrainerBox, 1, 2);
        } else if (member instanceof StudentMember) {
            uniField.setText(((StudentMember) member).getUniversityName());
            grid.add(new Label("University:"), 0, 2);
            grid.add(uniField, 1, 2);
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                member.setName(nameField.getText());
                member.setPhone(phoneField.getText());

                if (member instanceof PremiumMember) {
                    ((PremiumMember) member).setHasPersonalTrainer(hasTrainerBox.isSelected());
                } else if (member instanceof StudentMember) {
                    ((StudentMember) member).setUniversityName(uniField.getText());
                }
                return member;
            }
            return null;
        });

        dialog.showAndWait();
    }
}