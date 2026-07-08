# Markdown Table Editor

## Overview

MD-Table is a JavaFX application designed to visualize and edit Markdown tables for README.md files in a convenient and user-friendly way. Users can graphically edit tables and export them as clean Markdown.

## Screenshots

![Application Screenshot](https://github.com/NeuralCortex/MD_Table/blob/main/images/app.png)

![Parameter Editor Screenshot](https://github.com/NeuralCortex/MD_Table/blob/main/images/editor.png)

| Feature                  | Description                                                                 |
|--------------------------|-----------------------------------------------------------------------------|
| Create Table             | User-defined number of columns and rows                                     |
| Import & Export JSON     | Import and export your table structure as JSON                              |
| Parameter Editor         | Parameters defined here are replaced in the Markdown output. Great for making values like image sizes dynamic |
| Save as MD File          | Export your table as ready-to-use Markdown for README.md                   |

## Requirements

- Java Runtime Environment (JRE) or Java Development Kit (JDK) version 25
- JavaFX SDK for GUI functionality [JavaFX 26](https://gluonhq.com/products/javafx/)

## Usage Instructions

1. **Initialize the Grid**  
   Under the Properties panel on the right, enter your desired number of columns (Cols) and rows (Rows), then click **Create Table**.

2. **Edit Cell Content**  
   Click on any cell in the grid. Use the Cell Editor panel at the bottom-right to comfortably type or modify the text.

3. **Configure Alignment & Headers**  
   Select a column, choose your alignment (Left, Center, or Right), or change the header text in the Column Header field.

4. **Reorder Columns**  
   Reorder columns using drag and drop.

5. **Configure Parameters**  
   Define parameters to be replaced in the final Markdown table.

6. **Generate & Export**  
   Click **Build Markdown** to generate the raw Markdown code in the bottom panel. Use **Save As** or copy-paste the output directly into your Markdown document.

## Technologies Used

- **IDE**: Apache NetBeans 28 [NetBeans 28](https://netbeans.apache.org/)
- **Java SDK**: Java 25 [Java 25](https://www.oracle.com/java/technologies/downloads/#jdk25-windows)
- **GUI Development**: Gluon Scene Builder [Scene Builder 26](https://gluonhq.com/products/scene-builder/)
- **Framework**: JavaFX 26 [JavaFX 26](https://gluonhq.com/products/javafx/)