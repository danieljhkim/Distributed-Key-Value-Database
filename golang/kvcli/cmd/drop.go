/*
Copyright © 2025 danieljhkim
*/
package cmd

import (
	"fmt"

	"github.com/spf13/cobra"
)

var dropCmd = &cobra.Command{
	Use:   "drop",
	Short: "Remove all keys from the database",
	Args:  cobra.NoArgs,
	Run: func(cmd *cobra.Command, args []string) {
		fmt.Printf("Not implemented yet.")
		return
		response, err := kvClient.ExecuteCommand("KV CLEAR")
		if err != nil {
			fmt.Printf("Error: %v\n", err)
			return
		}
		fmt.Println(response)
	},
}

func init() {
	// rootCmd.AddCommand(dropCmd)
}
